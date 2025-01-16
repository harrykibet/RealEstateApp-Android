package com.application.real_estate_app.feature_property.data.apis

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.application.real_estate_app.core.data_utils.db_entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.mappers.toEntityModel
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.feature_property.domain.interfaces.IPropertyApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyApi @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val storageRef: FirebaseStorage // Injected via DI
) : IPropertyApi {

    override val uploadStatus = MutableLiveData<Boolean>()
    override val uploadError = MutableLiveData<String?>()

    override suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): Boolean {
        val propertyId = db.collection(FirestoreCollections.PROPERTIES).document().id // Always generate a new ID

        return try {
            uploadStatus.value = true // Uploading process started

            // 1. **Create the FireStore document first** with an initial state
            db.collection(FirestoreCollections.PROPERTIES)
                .document(propertyId)
                .set(property.toEntityModel().copy(id = propertyId)) // Make sure to store the ID as well
                .await()

            // 2. Upload images and videos, updating their URLs
            val imageUrls = uploadMedia(propertyId, imageUris, "images")
            val videoUrls = uploadMedia(propertyId, videoUris, "videos")

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
        } catch (e: Exception) {
            uploadStatus.value = false
            uploadError.value = e.message
            Log.e("PropertyApi", "Failed to upload property: ${e.message}")

            // **Clean up incomplete uploads or FireStore documents if necessary**
            db.collection(FirestoreCollections.PROPERTIES).document(propertyId).delete().await() // Optional cleanup
            false
        }
    }

    private suspend fun uploadMedia(
        propertyId: String,
        uris: List<Uri>,
        mediaType: String
    ): List<String> {
        val urls = mutableListOf<String>()
        uris.forEachIndexed { index, uri ->
            val filePath = "${FirestoreCollections.PROPERTIES}/$propertyId/$mediaType/${System.currentTimeMillis()}_$index"
            val fileRef = storageRef.reference.child(filePath) // Use DI-injected FirebaseStorage
            val downloadUrl = fileRef.putFile(uri).await().storage.downloadUrl.await().toString()
            urls.add(downloadUrl)
        }
        return urls
    }

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Boolean {
        return try {
            db.collection(FirestoreCollections.PROPERTIES).document(propertyId).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyApi", "Error updating property: ${e.message}")
            false
        }
    }

    override suspend fun deleteProperty(propertyId: String): Boolean {
        return try {
            db.collection(FirestoreCollections.PROPERTIES).document(propertyId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("PropertyApi", "Error deleting property: ${e.message}")
            false
        }
    }

    override suspend fun getPropertyById(propertyId: String): Property? {
        return try {
            val doc = db.collection(FirestoreCollections.PROPERTIES).document(propertyId).get().await()
            // Convert the data model (PropertyEntity) to domain model (Property)
            doc.toObject(PropertyEntity::class.java)?.toDomainModel()
        } catch (e: Exception) {
            Log.e("PropertyApi", "Error fetching property by ID: ${e.message}")
            null
        }
    }
}