package com.application.real_estate_app.core_network.sources

import com.application.real_estate_app.core_common.errors.Errors
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_network.db_entities.CommentEntity
import com.application.real_estate_app.core_network.db_names.FirestoreFields
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_model.Comment
import com.application.real_estate_app.core_network.db_names.FirestoreCollections
import com.application.real_estate_app.core_network.interfaces.ICommentsRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentsRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val network: INetworkHandler // Injected via DI
) : ICommentsRemoteDataSource {

    override fun listenForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Flow<List<Comment?>> {
        return callbackFlow {
            network.safeApiCallSuspend(
                apiCall = {
                    val listenerRegistration = db.collection(FirestoreCollections.PROPERTIES)
                        .document(propertyId)
                        .collection(FirestoreCollections.SubCollections.COMMENTS)
                        .orderBy(FirestoreFields.TIMESTAMP, Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                log(error.message)
                                onFailure(error) // Notify of the error
                                close(error) // Close the flow due to error
                            } else {
                                val comments = snapshot?.documents
                                    ?.map { it.toObject(CommentEntity::class.java)?.toDomainModel() }
                                    ?: emptyList()
                                trySend(comments) // Emit the comments list
                            }
                        }

                    // Close the listener when the flow is cancelled
                    awaitClose { listenerRegistration.remove() }
                },
                onFailure = { error ->
                    log(error.message)
                    onFailure(error) // Handle the failure through the provided callback
                    close(error) // Close the flow due to network failure
                }
            )
        }
    }

    override suspend fun submitComment(
        propertyId: String,
        comment: Comment,
        onFailure: (Exception) -> Unit
    ): Boolean? {
        return network.safeApiCallSuspend(
            apiCall = {
                try {
                    val commentsRef = db.collection(FirestoreCollections.PROPERTIES)
                        .document(propertyId)
                        .collection(FirestoreCollections.SubCollections.COMMENTS)
                        .document()

                    // Convert the domain model (Comment) to the data model (CommentEntity)
                    commentsRef.set(CommentEntity.fromDomainModel(comment)).await()
                    true // Return true if the comment was successfully submitted
                } catch (e: Exception) {
                    onFailure(e) // Pass detailed error context to the callback
                    log(e.message)
                    false // Return false if an error occurred during submission
                }
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            })
    }

    private fun log(message: String?) {
        logger.e("${Errors.COMMENTS_REPO} : $message")
    }
}
