package com.estatia.realestate.apps.core.network.sources

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
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


class FirestoreProperties @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val networkClient: INetworkClient
) : IPropertyRemoteDatasource {

    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> {

        return networkClient.execute {

            val propertyId =
                database.collection(PROPERTIES)
                    .document()
                    .id


            val images =
                uploadMedia(
                    propertyId,
                    imageUris,
                    MediaFormat.MEDIA_TYPE_IMAGES
                )


            val videos =
                uploadMedia(
                    propertyId,
                    videoUris,
                    MediaFormat.MEDIA_TYPE_VIDEOS
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
        propertyId: String,
        uris: List<Uri>,
        mediaType: String
    ): List<String> {

        return uris.map { uri ->


            val path =
                "$PROPERTIES/$propertyId/$mediaType/${UUID.randomUUID()}"


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


            val likesRef =
                database.collection(PROPERTIES)
                    .document(propertyId)
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

            }.await()


        }
    }

    override suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {

        return networkClient.execute {

            val likesRef =
                database.collection(PROPERTIES)
                    .document(propertyId)
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

            }.await()
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

    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyEntityModel>> {

        return networkClient.execute {

            val searchQuery =
                database.collection(PROPERTIES)
                    .orderBy(
                        FirestoreFields.TITLE,
                        Query.Direction.ASCENDING
                    )
                    .startAt(query)
                    .endAt(query + "\uf8ff")

            val querySnapshot =
                searchQuery.limit(limit.toLong()).get().await()

            querySnapshot.documents.mapNotNull {
                it.toObject(PropertyEntityModel::class.java)
            }
        }
    }
}
