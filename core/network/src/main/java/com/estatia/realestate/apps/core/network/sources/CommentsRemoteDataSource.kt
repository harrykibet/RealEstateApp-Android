package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.db_entities.CommentEntity
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.feature.Comment
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
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
) : ICommentsRemoteDataSource {

    override fun observeComments(
        propertyId: String
    ): Flow<List<CommentEntity>> = callbackFlow {

        val listenerRegistration = db.collection(FirestoreCollections.PROPERTIES)
            .document(propertyId)
            .collection(FirestoreCollections.SubCollections.COMMENTS)
            .orderBy(FirestoreFields.TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    log(error.message)
                    close(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents
                    ?.mapNotNull {
                        it.toObject(CommentEntity::class.java)
                    }
                    ?: emptyList()

                trySend(comments) // Send the list of comments to the flow
            }

        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun submitComment(
        comment: CommentEntity
    ): Result<Unit> {
        return try {
            val commentsRef = db.collection(FirestoreCollections.PROPERTIES)
                .document(comment.propertyId)
                .collection(FirestoreCollections.SubCollections.COMMENTS)
                .document()

            commentsRef
                .set(comment)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            log(e.message)
            Result.Error(e)
        }
    }


    private fun log(message: String?) {
        logger.e("${Errors.COMMENTS_REPO} : $message")
    }
}
