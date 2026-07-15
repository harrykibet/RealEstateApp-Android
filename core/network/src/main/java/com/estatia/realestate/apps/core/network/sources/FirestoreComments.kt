package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.network.core.RetryConfigs
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.IApiExecutor
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreComments @Inject constructor(
    private val db: FirebaseFirestore,
    private val apiExecutor: IApiExecutor,
    private val logger: LoggerInterface
) : ICommentsRemoteDataSource {


    override suspend fun submitComment(
        comment: CommentEntityModel
    ): Result<Unit> =
        apiExecutor.execute(RetryConfigs.COMMENTS) {

            commentsCollection(comment.propertyId)
                .document()
                .set(comment)
                .await()

            Unit
        }


    override fun observeComments(
        propertyId: String
    ): Flow<Result<List<CommentEntityModel>>> =
        callbackFlow {

            val listener =
                commentsCollection(propertyId)
                    .orderBy(
                        FirestoreFields.TIMESTAMP,
                        Query.Direction.DESCENDING
                    )
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {
                            trySend(
                                Result.Failure(error)
                            )
                            return@addSnapshotListener
                        }

                        val comments =
                            snapshot?.documents
                                ?.mapNotNull {
                                    it.toObject(CommentEntityModel::class.java)
                                }
                                ?: emptyList()

                        trySend(
                            Result.Success(comments)
                        )
                    }


            awaitClose {
                listener.remove()
            }
        }


    private fun commentsCollection(propertyId: String) =
        db.collection(FirestoreCollections.PROPERTIES)
            .document(propertyId)
            .collection(FirestoreCollections.SubCollections.COMMENTS)
}