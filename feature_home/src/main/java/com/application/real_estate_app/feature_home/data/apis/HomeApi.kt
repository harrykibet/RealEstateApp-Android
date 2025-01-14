package com.application.real_estate_app.feature_home.data.apis

import android.util.Log
import com.application.real_estate_app.core.data_utils.db_entities.LikesEntity
import com.application.real_estate_app.core.data_utils.db_entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.data_models.Likes
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.feature_home.domain.interfaces.IHomeApi
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class HomeApi @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val storageRef: FirebaseStorage // Injected via DI
): IHomeApi {

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
}