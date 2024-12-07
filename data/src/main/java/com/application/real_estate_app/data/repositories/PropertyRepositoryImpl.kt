package com.application.real_estate_app.data.repositories

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.application.real_estate_app.data.mappers.toDomainModel
import com.application.real_estate_app.data.models.CommentEntity
import com.application.real_estate_app.data.models.LikesEntity
import com.application.real_estate_app.data.models.PropertyEntity
import com.application.real_estate_app.domain.models.Likes
import com.application.real_estate_app.domain.models.Property
import com.application.real_estate_app.domain.models.Comment
import com.application.real_estate_app.data.mappers.toEntityModel
import com.application.real_estate_app.domain.interfaces.IPropertyRepository
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val storageRef: FirebaseStorage // Injected via DI
    ) : IPropertyRepository {

    override val uploadStatus = MutableLiveData<Boolean>()
    override val uploadError = MutableLiveData<String?>()

    override suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): Boolean {
        val propertyId = property.id ?: db.collection("properties").document().id // Always generate a new ID if null

        return try {
            uploadStatus.value = true // Uploading process started

            // 1. **Create the FireStore document first** with an initial state
            db.collection("properties")
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
            db.collection("properties")
                .document(propertyId)
                .set(updatedPropertyEntity, SetOptions.merge())
                .await()

            uploadStatus.value = false // Upload finished
            uploadError.value = null
            true
        } catch (e: Exception) {
            uploadStatus.value = false
            uploadError.value = e.message
            Log.e("PropertyRepository", "Failed to upload property: ${e.message}")

            // **Clean up incomplete uploads or FireStore documents if necessary**
            db.collection("properties").document(propertyId).delete().await() // Optional cleanup
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
            val filePath = "properties/$propertyId/$mediaType/${System.currentTimeMillis()}_$index"
            val fileRef = storageRef.reference.child(filePath) // Use DI-injected FirebaseStorage
            val downloadUrl = fileRef.putFile(uri).await().storage.downloadUrl.await().toString()
            urls.add(downloadUrl)
        }
        return urls
    }

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("properties").document(propertyId).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error updating property: ${e.message}")
            false
        }
    }

    override suspend fun deleteProperty(propertyId: String): Boolean {
        return try {
            db.collection("properties").document(propertyId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error deleting property: ${e.message}")
            false
        }
    }

    override suspend fun getPropertyById(propertyId: String): Property? {
        return try {
            val doc = db.collection("properties").document(propertyId).get().await()
            // Convert the data model (PropertyEntity) to domain model (Property)
            doc.toObject(PropertyEntity::class.java)?.toDomainModel()
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error fetching property by ID: ${e.message}")
            null
        }
    }

    // Updated implementation of fetchPropertiesPaginated
    override suspend fun fetchPropertiesPaginated(
        lastVisible: String?,  // Use String instead of DocumentSnapshot
        pageSize: Int
    ): Pair<List<Property>, String?> {
        return try {
            // Build the FireStore query
            val query = if (lastVisible == null) {
                db.collection("properties")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
            } else {
                db.collection("properties")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)  // Start after the last document fetched
                    .limit(pageSize.toLong())
            }

            // Execute the query and fetch the results
            val snapshot = query.get().await()

            // Convert the data models (PropertyEntity) to domain models (Property)
            val properties = snapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }

            // Get the last document ID to fetch the next set of properties
            val newLastVisible = snapshot.documents.lastOrNull()?.id

            // Return the properties and the last document ID
            Pair(properties, newLastVisible)
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error fetching paginated properties: ${e.message}")
            // Return an empty list and null for the last visible document in case of error
            Pair(emptyList(), null)
        }
    }

    override suspend fun fetchLikedProperties(userId: String): List<Property> {
        return try {
            val likedPropertyIds = db.collection("users")
                .document(userId)
                .collection("likedProperties")
                .get()
                .await()
                .documents.map { it.id }

            if (likedPropertyIds.isNotEmpty()) {
                val propertiesSnapshot = db.collection("properties")
                    .whereIn(FieldPath.documentId(), likedPropertyIds)
                    .get()
                    .await()

                propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error fetching liked properties: ${e.message}")
            emptyList()
        }
    }

    override suspend fun toggleLikeProperty(userId: String, propertyId: String): Boolean {
        return try {
            val likesRef = db.collection("properties").document(propertyId)
                .collection("likes").document(userId)
            val likedPropertiesRef = db.collection("users").document(userId)
                .collection("likedProperties").document(propertyId)

            val isLiked = likedPropertiesRef.get().await().exists()

            db.runBatch { batch ->
                if (isLiked) {
                    batch.delete(likesRef)
                    batch.delete(likedPropertiesRef)
                } else {
                    val likeData = Likes(userId, Date()) // Domain model (Likes)
                    batch.set(likesRef, LikesEntity.fromDomainModel(likeData)) // Convert to data model
                    batch.set(likedPropertiesRef, LikesEntity.fromDomainModel(likeData)) // Convert to data model
                }
            }.await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error toggling like: ${e.message}")
            false
        }
    }

    override fun listenForComments(
        propertyId: String,
        onError: (Exception) -> Unit
    ): Flow<List<Comment?>> {
        return callbackFlow {
            val listenerRegistration = db.collection("properties")
                .document(propertyId)
                .collection("comments")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        onError(error)
                        close(error) // Close the flow in case of error
                    } else {
                        val comments = snapshot?.documents
                            ?.map { it.toObject(CommentEntity::class.java)?.toDomainModel() }
                            ?: emptyList()
                        trySend(comments) // Emit the comments list
                    }
                }

            // Close the listener when the flow is cancelled
            awaitClose { listenerRegistration.remove() }
        }
    }

    override suspend fun submitComment(
        propertyId: String,
        comment: Comment
    ): Boolean {
        return try {
            val commentsRef = db.collection("properties")
                .document(propertyId)
                .collection("comments")
                .document()

            // Convert the domain model (Comment) to the data model (CommentEntity)
            commentsRef.set(CommentEntity.fromDomainModel(comment)).await()
            true
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error submitting comment: ${e.message}")
            false
        }
    }

    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): List<Property> {
        return try {
            val propertiesSnapshot = db.collection("properties")
                .whereEqualTo("title", query)
                .limit(limit.toLong())
                .get()
                .await()

            propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error searching properties: ${e.message}")
            emptyList()
        }
    }
}
