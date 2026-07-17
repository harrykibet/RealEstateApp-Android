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
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


class FirestoreProperties @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val networkClient: INetworkClient
) : IPropertyRemoteDatasource {


    private val _uploadStatus =
        MutableStateFlow(false)

    override val uploadStatus: StateFlow<Boolean> =
        _uploadStatus.asStateFlow()


    private val _uploadError =
        MutableStateFlow<String?>(null)

    override val uploadError: StateFlow<String?> =
        _uploadError.asStateFlow()


    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): Result<String> {


        return networkClient.execute {


            val propertyId =
                database.collection(PROPERTIES)
                    .document()
                    .id


            database.collection(PROPERTIES)
                .document(propertyId)
                .set(
                    property.copy(
                        id = propertyId
                    )
                )
                .await()


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


            database.collection(PROPERTIES)
                .document(propertyId)
                .update(
                    mapOf(
                        FirestoreFields.IMAGE_URL to images,
                        FirestoreFields.VIDEO_URL to videos
                    )
                )
                .await()


            propertyId
        }
    }

    private suspend fun uploadMedia(
        propertyId:String,
        uris:List<Uri>,
        mediaType:String
    ):List<String>{

        val urls =
            mutableListOf<String>()


        uris.forEachIndexed { index, uri ->


            val path =
                "$PROPERTIES/$propertyId/$mediaType/$index"


            val url =
                storage.reference
                    .child(path)
                    .putFile(uri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
                    .toString()


            urls.add(url)
        }


        return urls
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): Result<Unit> {

        return networkClient.execute {

            database.collection(PROPERTIES)
                .document(propertyId)
                .update(updates)
                .await()

        }
    }

    override suspend fun deleteProperty(
        propertyId: String
    ): Result<Unit> {
        return networkClient.execute {

            database.collection(PROPERTIES)
                .document(propertyId)
                .delete()
                .await()
        }
    }

    override suspend fun getPropertyById(
        propertyId: String
    ): Result<PropertyEntityModel> {
        return networkClient.execute {
            database.collection(PROPERTIES)
                .document(propertyId)
                .get()
                .await()
                .toObject(PropertyEntityModel::class.java)
                ?: throw FirebaseFirestoreException(
                    "Property not found",
                    FirebaseFirestoreException.Code.NOT_FOUND
                )
        }
    }

    override suspend fun fetchLikedProperties(
        userId: String
    ): Result<List<PropertyEntityModel>> {
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
    ): Result<Unit> {


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


            val likeData =
                LikesDomainModel(
                    userId,
                    Date()
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
    ): Result<Unit> {

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

    // Updated implementation of fetchPropertiesPaginated
    override suspend fun fetchPropertiesPaginated(
        lastVisible:String?,
        pageSize:Int
    ):Result<Pair<List<PropertyEntityModel>,String?>> {

        return networkClient.execute {


            val query =
                if(lastVisible == null){

                    database.collection(PROPERTIES)
                        .orderBy(
                            FirestoreFields.CREATED_AT,
                            Query.Direction.DESCENDING
                        )
                        .limit(pageSize.toLong())

                } else {

                    database.collection(PROPERTIES)
                        .orderBy(
                            FirestoreFields.CREATED_AT,
                            Query.Direction.DESCENDING
                        )
                        .startAfter(lastVisible)
                        .limit(pageSize.toLong())
                }


            val snapshot =
                query.get().await()


            val properties =
                snapshot.documents.mapNotNull {
                    it.toObject(PropertyEntityModel::class.java)
                }


            val cursor =
                snapshot.documents.lastOrNull()?.id


            properties to cursor
        }
    }

    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): Result<List<PropertyEntityModel>> {

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
