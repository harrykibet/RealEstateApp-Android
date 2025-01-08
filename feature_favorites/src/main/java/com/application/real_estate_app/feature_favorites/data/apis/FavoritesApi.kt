package com.application.real_estate_app.feature_favorites.data.apis

import android.util.Log
import com.application.real_estate_app.core.data_utils.entities.LikesEntity
import com.application.real_estate_app.core.data_utils.entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.models.Likes
import com.application.real_estate_app.core.data_utils.models.Property
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFavoritesApi
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FavoritesApi @Inject constructor(
    private val db: FirebaseFirestore // Injected via DI
): IFavoritesApi {


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
}