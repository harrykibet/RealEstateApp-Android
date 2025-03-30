package com.application.real_estate_app.core_network.sources

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.application.real_estate_app.core_common.errors.Errors
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_network.db_entities.LikesEntity
import com.application.real_estate_app.core_network.db_entities.PropertyEntity
import com.application.real_estate_app.core_network.db_names.FirestoreFields
import com.application.real_estate_app.core_network.mappers.toDomainModel
import com.application.real_estate_app.core_network.mappers.toEntityModel
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_model.feature.Likes
import com.application.real_estate_app.core_model.property.Property
import com.application.real_estate_app.core_common.media.MediaFormat
import com.application.real_estate_app.core_network.db_names.FirestoreCollections
import com.application.real_estate_app.core_network.interfaces.IPropertyRemoteDatasource
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class PropertyRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val storageRef: FirebaseStorage, // Injected via DI
    private val network: INetworkHandler // Injected via DI
) : IPropertyRemoteDatasource {

    override val uploadStatus = MutableLiveData<Boolean>()
    override val uploadError = MutableLiveData<String?>()

    override suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean? {
        val propertyId = db.collection(FirestoreCollections.PROPERTIES).document().id // Always generate a new ID

        return network.safeApiCallSuspend(
            apiCall = {
                uploadStatus.value = true // Uploading process started

                // 1. **Create the FireStore document first** with an initial state
                db.collection(FirestoreCollections.PROPERTIES)
                    .document(propertyId)
                    .set(property.toEntityModel().copy(id = propertyId)) // Make sure to store the ID as well
                    .await()

                // 2. Upload images and videos, updating their URLs
                val imageUrls = uploadMedia(propertyId, imageUris, MediaFormat.MEDIA_TYPE_IMAGES, onFailure)
                val videoUrls = uploadMedia(propertyId, videoUris, MediaFormat.MEDIA_TYPE_VIDEOS, onFailure)

                // 3. Update the FireStore document with media URLs
                val updatedPropertyEntity = property.toEntityModel().copy(
                    id = propertyId,
                    imageUrl = imageUrls,
                    videoUrl = videoUrls
                )
                db.collection(FirestoreCollections.PROPERTIES)
                    .document(propertyId)
                    .set(updatedPropertyEntity, SetOptions.merge())
                    .await()

                uploadStatus.value = false // Upload finished
                uploadError.value = null
                true
            }, onFailure = { exception ->
                uploadStatus.value = false // Upload failed
                uploadError.value = exception.message
                onFailure(exception)
                log(exception.message)
            })
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
                    val filePath = "${FirestoreCollections.PROPERTIES}/$propertyId/$mediaType/${System.currentTimeMillis()}_$index"
                    val fileRef = storageRef.reference.child(filePath) // Use DI-injected FirebaseStorage
                    val downloadUrl = fileRef.putFile(uri).await().storage.downloadUrl.await().toString()
                    urls.add(downloadUrl)
                }
                urls
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: emptyList()
    }

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>, onFailure: (Exception) -> Unit): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                db.collection(FirestoreCollections.PROPERTIES).document(propertyId).update(updates).await()
                true
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: false
    }

    override suspend fun deleteProperty(propertyId: String, onFailure: (Exception) -> Unit): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                db.collection(FirestoreCollections.PROPERTIES).document(propertyId).delete().await()
                true
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: false
    }

    override suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property? {
        return network.safeApiCallSuspend(
            apiCall = {
                val doc = db.collection(FirestoreCollections.PROPERTIES).document(propertyId).get().await()
                // Convert the data model (PropertyEntity) to domain model (Property)
                doc.toObject(PropertyEntity::class.java)?.toDomainModel()
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            })
    }

    override suspend fun fetchLikedProperties(userId: String, onFailure: (Exception) -> Unit): List<Property>? {
        return network.safeApiCallSuspend(
            apiCall = {
                val likedPropertyIds = db.collection(FirestoreCollections.USERS)
                    .document(userId)
                    .collection(FirestoreCollections.SubCollections.LIKED_PROPERTIES)
                    .get()
                    .await()
                    .documents.map { it.id }

                if (likedPropertyIds.isNotEmpty()) {
                    val propertiesSnapshot = db.collection(FirestoreCollections.PROPERTIES)
                        .whereIn(FieldPath.documentId(), likedPropertyIds)
                        .get()
                        .await()

                    propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
                } else {
                    emptyList()
                }
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            })
    }

    override suspend fun toggleLikeProperty(userId: String, propertyId: String, onFailure: (Exception) -> Unit): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                val likesRef = db.collection(FirestoreCollections.PROPERTIES).document(propertyId)
                    .collection(FirestoreCollections.SubCollections.LIKES).document(userId)
                val likedPropertiesRef = db.collection(FirestoreCollections.USERS).document(userId)
                    .collection(FirestoreCollections.SubCollections.LIKED_PROPERTIES).document(propertyId)

                val isLiked = likedPropertiesRef.get().await().exists()

                db.runBatch { batch ->
                    if (isLiked) {
                        batch.delete(likesRef)
                        batch.delete(likedPropertiesRef)
                    } else {
                        val likeData = Likes(userId, Date())
                        batch.set(likesRef, LikesEntity.fromDomainModel(likeData))
                        batch.set(likedPropertiesRef, LikesEntity.fromDomainModel(likeData))
                    }
                }.await()
                true
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
            }) ?: false
    }

    // Updated implementation of fetchPropertiesPaginated
    override suspend fun fetchPropertiesPaginated(
        lastVisible: String?,  // Use String instead of DocumentSnapshot
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<Property>, String?> {
        return network.safeApiCallSuspend(
            apiCall = {
                val query = if (lastVisible == null) {
                    db.collection(FirestoreCollections.PROPERTIES)
                        .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                        .limit(pageSize.toLong())
                } else {
                    db.collection(FirestoreCollections.PROPERTIES)
                        .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                        .startAfter(lastVisible)  // Start after the last document fetched
                        .limit(pageSize.toLong())
                }

                val snapshot = query.get().await()

                val properties = snapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }

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
    ): List<Property> {
        return network.safeApiCallSuspend(
            apiCall = {
                val propertiesSnapshot = db.collection(FirestoreCollections.PROPERTIES)
                    .whereEqualTo(FirestoreFields.TITLE, query)
                    .limit(limit.toLong())
                    .get()
                    .await()

                propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
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
