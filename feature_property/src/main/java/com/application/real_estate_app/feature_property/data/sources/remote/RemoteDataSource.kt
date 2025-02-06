package com.application.real_estate_app.feature_property.data.sources.remote


import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.application.real_estate_app.core.data.db_entities.PropertyEntity
import com.application.real_estate_app.core.data.mappers.toDomainModel
import com.application.real_estate_app.core.data.mappers.toEntityModel
import com.application.real_estate_app.core.domain.models.Property
import com.application.real_estate_app.core.data.db_names.FirestoreCollections
import com.application.real_estate_app.core.common.errors.Errors
import com.application.real_estate_app.core.domain.interfaces.INetworkHandler
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.feature_property.utils.MediaStrings
import com.application.real_estate_app.feature_property.domain.interfaces.IRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val storageRef: FirebaseStorage, // Injected via DI
    private val network: INetworkHandler // Injected via DI
) : IRemoteDataSource {

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
            val imageUrls = uploadMedia(propertyId, imageUris, MediaStrings.MEDIA_TYPE_IMAGES, onFailure)
            val videoUrls = uploadMedia(propertyId, videoUris, MediaStrings.MEDIA_TYPE_VIDEOS, onFailure)

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

    private fun log(message: String?) {
        logger.e("${Errors.PROPERTY_REPO}: $message")
    }
}
