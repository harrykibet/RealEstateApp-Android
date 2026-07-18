package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfigs
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IFirestoreErrorMapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreComments @Inject constructor(
    private val database: FirebaseFirestore,
    private val networkClient: INetworkClient,
    private val errorMapper: IFirestoreErrorMapper
) : ICommentsRemoteDataSource {


    override suspend fun submitComment(
        comment: CommentEntityModel
    ): AppResult<Unit> =
        networkClient.execute(RetryConfigs.COMMENTS) {

            commentsCollection(comment.propertyId)
                .document()
                .set(comment)
                .await()
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