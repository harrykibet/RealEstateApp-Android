package com.application.real_estate_app.feature_comments.data.apis

import android.net.ConnectivityManager
import android.util.Log
import com.application.real_estate_app.core.data_utils.db_entities.CommentEntity
import com.application.real_estate_app.core.data_utils.data_models.Comment
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.errors.ErrorMessages
import com.application.real_estate_app.core.network_utils.NetworkHandler
import com.application.real_estate_app.feature_comments.domain.interfaces.ICommentsApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentsApi @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val connectivityManager: ConnectivityManager // Injected via DI
) : ICommentsApi {

    override fun listenForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Flow<List<Comment?>> {
        return callbackFlow {
            // Using safeApiCallSuspend to check for internet connectivity
            val networkStatus = NetworkHandler.safeApiCallSuspend(connectivityManager =
            connectivityManager,
                action = {
                true // No need for any action, just check the network status
            },
                onFailure = { exception ->
                onFailure(exception) // Provide error callback if network check fails
                close(exception) // Close flow on error
            })

            if (networkStatus != null) {
                // If network check passes, start listening for comments
                val listenerRegistration = db.collection(FirestoreCollections.PROPERTIES)
                    .document(propertyId)
                    .collection(FirestoreCollections.SubCollections.COMMENTS)
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            val errorMessage = "Error fetching comments: ${error.localizedMessage}"
                            onFailure(error) // Provide more context to onFailure
                            Log.e("CommentsApi", errorMessage)
                            close(error) // Close the flow in case of error
                        } else {
                            val comments = snapshot?.documents
                                ?.map { it.toObject(CommentEntity::class.java)?.toDomainModel() }
                                ?: emptyList()
                            trySend(comments) // Emit the comments list
                        }
                    }

                // Close the listener when the flow is cancelled
                awaitClose { listenerRegistration.remove() }

            } else {
                // No internet access, provide more specific error context
                val errorMessage = ErrorMessages.NO_INTERNET_CONNECTION
                onFailure(Exception(errorMessage)) // Pass detailed error context
                close(Exception(errorMessage)) // Close with error
            }
        }
    }

    override suspend fun submitComment(
        propertyId: String,
        comment: Comment,
        onFailure: (Exception) -> Unit
    ): Boolean? {
        return NetworkHandler.safeApiCallSuspend(connectivityManager = connectivityManager,
            action = {
            try {
                val commentsRef = db.collection(FirestoreCollections.PROPERTIES)
                    .document(propertyId)
                    .collection(FirestoreCollections.SubCollections.COMMENTS)
                    .document()

                // Convert the domain model (Comment) to the data model (CommentEntity)
                commentsRef.set(CommentEntity.fromDomainModel(comment)).await()
                true // Return true if the comment was successfully submitted
            } catch (e: Exception) {
                val errorMessage = "Error submitting comment: ${e.message}"
                onFailure(e) // Pass detailed error context to the callback
                Log.e("CommentsApi", errorMessage)
                false // Return false if an error occurred during submission
            }
        },
            onFailure = { exception ->
            onFailure(exception)
            exception.message?.let { Log.e("CommentsApi", "Network error:$it") }
        })
    }
}
