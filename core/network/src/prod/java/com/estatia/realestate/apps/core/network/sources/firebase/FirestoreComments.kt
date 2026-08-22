package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields.COMMENTS_COUNT
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfigs
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IDatabaseErrorMapper
import com.estatia.realestate.apps.core.network.di.FirebaseMapper
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class FirestoreComments @Inject constructor(
    private val database: FirebaseFirestore,
    private val networkClient: INetworkClient,
    @FirebaseMapper private val errorMapper: IDatabaseErrorMapper
) : ICommentsRemoteDataSource {


    override suspend fun submitComment(
        comment: CommentEntityModel
    ): AppResult<Unit> =
        networkClient.execute(RetryConfigs.COMMENTS) {

            val propertyRef = database.collection(FirestoreCollections.PROPERTIES)
                .document(comment.propertyId)

            val commentRef = propertyRef
                .collection(FirestoreCollections.SubCollections.COMMENTS)
                .document()

            database.runBatch { batch ->
                batch.set(commentRef, comment)
                batch.update(propertyRef, COMMENTS_COUNT, FieldValue.increment(1))
            }.await()
        }


    override fun observeComments(
        propertyId: String
    ): Flow<AppResult<List<CommentEntityModel>>> =
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
                                AppResult.Error(
                                    errorMapper.map(error)
                                )
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
                            AppResult.Success(comments)
                        )
                    }


            awaitClose {
                listener.remove()
            }
        }


    private fun commentsCollection(propertyId: String) =
        database.collection(FirestoreCollections.PROPERTIES)
            .document(propertyId)
            .collection(FirestoreCollections.SubCollections.COMMENTS)
}
