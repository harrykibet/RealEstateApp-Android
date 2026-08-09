package com.estatia.realestate.apps.core.network.sources.firebase

import android.net.Uri
import com.estatia.realestate.apps.core.common.media.MediaFormat
import com.estatia.realestate.apps.core.model.feature.LikesDomainModel
import com.estatia.realestate.apps.core.network.db_entities.LikesEntity
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKED_PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.USERS
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields.LIKES_COUNT
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields.SHARES_COUNT
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields.VIEWS_COUNT
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


internal class FirestoreProperties @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val networkClient: INetworkClient
) : IPropertyRemoteDatasource {

    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> {

        // IDEMPOTENCY: Use the client-provided ID or fallback to generating one once.
        // This ensures retries don't create duplicate documents.
        val propertyId = property.id.ifBlank { UUID.randomUUID().toString() }

        val imagePaths = imageUris.map {
            "$PROPERTIES/$propertyId/${MediaFormat.MEDIA_TYPE_IMAGES}/${UUID.randomUUID()}"
        }

        val videoPaths = videoUris.map {
            "$PROPERTIES/$propertyId/${MediaFormat.MEDIA_TYPE_VIDEOS}/${UUID.randomUUID()}"
        }

        return networkClient.execute {

            val images =
                uploadMedia(
                    imageUris,
                    imagePaths
                )


            val videos =
                uploadMedia(
                    videoUris,
                    videoPaths
                )


            val finalProperty =
                property.copy(
                    id = propertyId,
                    imageUrl = images,
                    videoUrl = videos
                )


            database.collection(PROPERTIES)
                .document(propertyId)
                .set(finalProperty)
                .await()


            propertyId
        }
    }

    private suspend fun uploadMedia(
        uris: List<Uri>,
        paths: List<String>
    ): List<String> {

        return uris.mapIndexed { index, uri ->

            val path = paths[index]

            storage.reference
                .child(path)
                .putFile(uri)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()
        }
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit> {

        return networkClient.execute {

            database.collection(PROPERTIES)
                .document(propertyId)
                .update(updates)
                .await()

        }
    }

    override suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit> {
        return networkClient.execute {

            database.collection(PROPERTIES)
                .document(propertyId)
                .delete()
                .await()
        }
    }

    override suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyEntityModel> {

        return networkClient.execute {

            val snapshot =
                database.collection(PROPERTIES)
                    .document(propertyId)
                    .get()
                    .await()


            if(!snapshot.exists()) {
                throw DatabaseException.NotFound
            }


            snapshot.toObject(PropertyEntityModel::class.java)
                ?: throw DatabaseException.InvalidData(
                    "Unable to parse property"
                )
        }
    }

    override suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyEntityModel>> {

        return networkClient.execute {

            val likedPropertyIds = database.collection(USERS)
                .document(userId)
                .collection(LIKED_PROPERTIES)
                .get()
                .await()
                .documents.map { it.id }

            if (likedPropertyIds.isNotEmpty()) {
                val propertiesSnapshot = database.collection(PROPERTIES)
                    .whereIn(FieldPath.documentId(), likedPropertyIds)
                    .get()
                    .await()

                propertiesSnapshot.documents.mapNotNull {
                    it.toObject(PropertyEntityModel::class.java)
                }
            } else {
                emptyList()
            }
        }
    }

    override suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {


        return networkClient.execute {


            val propertyRef =
                database.collection(PROPERTIES)
                    .document(propertyId)

            val likesRef =
                propertyRef
                    .collection(LIKES)
                    .document(userId)


            val likedPropertiesRef =
                database.collection(USERS)
                    .document(userId)
                    .collection(LIKED_PROPERTIES)
                    .document(propertyId)


            val likeData = LikesDomainModel(
                userId,
                System.currentTimeMillis()
            )

            database.runBatch { batch ->

                batch.set(
                    likesRef,
                    LikesEntity.fromDomainModel(
                        likeData
                    )
                )


                batch.set(
                    likedPropertiesRef,
                    LikesEntity.fromDomainModel(
                        likeData
                    )
                )

                batch.update(propertyRef, LIKES_COUNT, FieldValue.increment(1))

            }.await()


        }
    }

    override suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {

        return networkClient.execute {

            val propertyRef =
                database.collection(PROPERTIES)
                    .document(propertyId)

            val likesRef =
                propertyRef
                    .collection(LIKES)
                    .document(userId)


            val likedPropertiesRef =
                database.collection(USERS)
                    .document(userId)
                    .collection(LIKED_PROPERTIES)
                    .document(propertyId)


            database.runBatch { batch ->

                batch.delete(likesRef)
                batch.delete(likedPropertiesRef)
                
                batch.update(propertyRef, LIKES_COUNT, FieldValue.increment(-1))

            }.await()
        }
    }

    override suspend fun recordView(propertyId: String): AppResult<Unit> {
        return networkClient.execute {
            database.collection(PROPERTIES)
                .document(propertyId)
                .update(VIEWS_COUNT, FieldValue.increment(1))
                .await()
        }
    }

    override suspend fun recordShare(propertyId: String): AppResult<Unit> {
        return networkClient.execute {
            database.collection(PROPERTIES)
                .document(propertyId)
                .update(SHARES_COUNT, FieldValue.increment(1))
                .await()
        }
    }

    override suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage> {


        return networkClient.execute {


            val query =
                database.collection(PROPERTIES)
                    .orderBy(
                        FirestoreFields.CREATED_AT,
                        Query.Direction.DESCENDING
                    )
                    .let {

                        if(cursor != null)
                            it.startAfter(
                                cursor.createdAt,
                                cursor.documentId
                            )
                        else
                            it

                    }
                    .limit(pageSize.toLong())


            val snapshot =
                query.get().await()


            val properties =
                snapshot.documents.mapNotNull {
                    it.toObject(
                        PropertyEntityModel::class.java
                    )
                }


            val last =
                snapshot.documents.lastOrNull()


            PropertyRemotePage(
                properties = properties,
                cursor =
                    last?.let {
                        PropertyCursor(
                            createdAt =
                                it.getLong(
                                    FirestoreFields.CREATED_AT
                                ) ?: 0L,
                            documentId =
                                it.id
                        )
                    }
            )
        }
    }
}
