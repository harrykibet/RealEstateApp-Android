package com.application.real_estate_app.feature_comments.data.repositories

import android.util.Log
import com.application.real_estate_app.core.data_utils.entities.CommentEntity
import com.application.real_estate_app.core.data_utils.models.Comment
import com.application.real_estate_app.feature_comments.domain.interfaces.IFeatureCommentsRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeatureCommentsRepo @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
) : IFeatureCommentsRepo {
    override fun listenForComments(
        propertyId: String,
        onError: (Exception) -> Unit
    ): Flow<List<Comment?>> {
        return callbackFlow {
            val listenerRegistration = db.collection("properties")
                .document(propertyId)
                .collection("comments")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        onError(error)
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
        }
    }

    override suspend fun submitComment(
        propertyId: String,
        comment: Comment
    ): Boolean {
        return try {
            val commentsRef = db.collection("properties")
                .document(propertyId)
                .collection("comments")
                .document()

            // Convert the domain model (Comment) to the data model (CommentEntity)
            commentsRef.set(CommentEntity.fromDomainModel(comment)).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error submitting comment: ${e.message}")
            false
        }
    }
}