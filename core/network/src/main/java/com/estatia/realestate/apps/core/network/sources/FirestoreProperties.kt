package com.estatia.realestate.apps.core.network.sources

import android.net.Uri
import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.common.media.MediaFormat
import com.estatia.realestate.apps.core.model.feature.Likes
import com.estatia.realestate.apps.core.network.db_entities.LikesEntity
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKED_PROPERTIES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.SubCollections.LIKES
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections.USERS
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


class FirestoreProperties @Inject constructor(
    private val db: FirebaseFirestore,
    private val logger: LoggerInterface,
    private val storageRef: FirebaseStorage,
    private val network: INetworkHandler
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
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean? {


        val propertyId =
            db.collection(PROPERTIES)
                .document()
                .id


        return network.safeApiCallSuspend(
            apiCall = {


                _uploadStatus.value = true
                _uploadError.value = null


                try {


                    /*
                     * Create initial Firestore document
                     */
                    db.collection(PROPERTIES)
                        .document(propertyId)
                        .set(
                            property.copy(
                                id = propertyId
                            )
                        )
                        .await()


                    /*
                     * Upload media
                     */
                    val imageUrls =
                        uploadMedia(
                            propertyId,
                            imageUris,
                            MediaFormat.MEDIA_TYPE_IMAGES,
                            onFailure
                        )


                    val videoUrls =
                        uploadMedia(
                            propertyId,
                            videoUris,
                            MediaFormat.MEDIA_TYPE_VIDEOS,
                            onFailure
                        )


                    /*
                     * Update media URLs
                     */
                    db.collection(PROPERTIES)
                        .document(propertyId)
                        .set(
                            property.copy(
                                id = propertyId,
                                imageUrl = imageUrls,
                                videoUrl = videoUrls
                            ),
                            SetOptions.merge()
                        )
                        .await()



                    true


                } finally {

                    _uploadStatus.value = false
                }


            },

            onFailure = { exception ->

                _uploadError.value =
                    exception.message ?: "Unknown upload error"


                onFailure(exception)

                logger.e(
                    exception.message ?: "Upload failed"
                )
            }
        )
    }

    private suspend fun uploadMedia(
        propertyId: String,
        uris: List<Uri>,
        mediaType: String,
        onFailure: (Exception) -> Unit
    ): List<String> {
        return network.safeApiCallSuspend(
            apiCall = {
                val urls = mutableListOf<String>()
                uris.forEachIndexed { index, uri ->
                    val filePath =
                        "${PROPERTIES}/$propertyId/$mediaType/${System.currentTimeMillis()}_$index"
                    val fileRef =
                        storageRef.reference.child(filePath) // Use DI-injected FirebaseStorage
                    val downloadUrl =
                        fileRef.putFile(uri).await().storage.downloadUrl.await().toString()
                    urls.add(downloadUrl)
                }
                urls
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: emptyList()
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                db.collection(PROPERTIES).document(propertyId).update(updates).await()
                true
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: false
    }

    override suspend fun deleteProperty(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                db.collection(PROPERTIES).document(propertyId).delete().await()
                true
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: false
    }

    override suspend fun getPropertyById(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): PropertyEntityModel? {
        return network.safeApiCallSuspend(
            apiCall = {
                val doc = db.collection(PROPERTIES).document(propertyId).get().await()
                // Convert the data model (PropertyEntity) to domain model (Property)
                doc.toObject(PropertyEntityModel::class.java)
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            })
    }

    override suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<PropertyEntityModel?>? {
        return network.safeApiCallSuspend(
            apiCall = {
                val likedPropertyIds = db.collection(USERS)
                    .document(userId)
                    .collection(LIKED_PROPERTIES)
                    .get()
                    .await()
                    .documents.map { it.id }

                if (likedPropertyIds.isNotEmpty()) {
                    val propertiesSnapshot = db.collection(PROPERTIES)
                        .whereIn(FieldPath.documentId(), likedPropertyIds)
                        .get()
                        .await()

                    propertiesSnapshot.documents.map {
                        it.toObject(PropertyEntityModel::class.java)
                    }
                } else {
                    emptyList()
                }
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            })
    }

    override suspend fun likeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean = network.safeApiCallSuspend(
        apiCall = {
            val likesRef = db.collection(PROPERTIES)
                .document(propertyId)
                .collection(LIKES)
                .document(userId)

            val likedPropertiesRef = db.collection(USERS)
                .document(userId)
                .collection(LIKED_PROPERTIES)
                .document(propertyId)

            val likeData = Likes(userId, Date())

            db.runBatch { batch ->
                batch.set(likesRef, LikesEntity.fromDomainModel(likeData))
                batch.set(likedPropertiesRef, LikesEntity.fromDomainModel(likeData))
            }.await()

            true
        },
        onFailure = {
            onFailure(it)
            log(it.message)
        }
    ) ?: false

    override suspend fun unlikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean = network.safeApiCallSuspend(
        apiCall = {
            val likesRef = db.collection(PROPERTIES)
                .document(propertyId)
                .collection(LIKES)
                .document(userId)

            val likedPropertiesRef = db.collection(USERS)
                .document(userId)
                .collection(LIKED_PROPERTIES)
                .document(propertyId)

            db.runBatch { batch ->
                batch.delete(likesRef)
                batch.delete(likedPropertiesRef)
            }.await()

            true
        },
        onFailure = {
            onFailure(it)
            log(it.message)
        }
    ) ?: false


    // Updated implementation of fetchPropertiesPaginated
    override suspend fun fetchPropertiesPaginated(
        lastVisible: String?,  // Use String instead of DocumentSnapshot
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<PropertyEntityModel?>, String?> {
        return network.safeApiCallSuspend(
            apiCall = {
                val query = if (lastVisible == null) {
                    db.collection(PROPERTIES)
                        .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                        .limit(pageSize.toLong())
                } else {
                    db.collection(PROPERTIES)
                        .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                        .startAfter(lastVisible)  // Start after the last document fetched
                        .limit(pageSize.toLong())
                }

                val snapshot = query.get().await()

                val properties = snapshot.documents.map {
                    it.toObject(PropertyEntityModel::class.java)
                }

                val newLastVisible = snapshot.documents.lastOrNull()?.id

                Pair(properties, newLastVisible)
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: Pair(emptyList(), null)
    }

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<PropertyEntityModel?> {
        return network.safeApiCallSuspend(
            apiCall = {
                val propertiesSnapshot = db.collection(PROPERTIES)
                    .whereEqualTo(FirestoreFields.TITLE, query)
                    .limit(limit.toLong())
                    .get()
                    .await()

                propertiesSnapshot.documents.map {
                    it.toObject(PropertyEntityModel::class.java)
                }
            },
            onFailure = { e ->
                log(e.message)
                onFailure(e)
            }
        ) ?: emptyList()
    }

    private fun log(message: String?) {
        logger.e("${Errors.PROPERTY_REPO}: $message")
    }
}
