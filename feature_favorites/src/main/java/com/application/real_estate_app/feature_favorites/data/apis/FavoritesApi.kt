package com.application.real_estate_app.feature_favorites.data.apis

import android.util.Log
import com.application.real_estate_app.core.data_utils.db_entities.LikesEntity
import com.application.real_estate_app.core.data_utils.db_entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.data_models.Likes
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
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
            val doc = db.collection(FirestoreCollections.PROPERTIES).document(propertyId).get().await()
            // Convert the data model (PropertyEntity) to domain model (Property)
            doc.toObject(PropertyEntity::class.java)?.toDomainModel()
        } catch (e: Exception) {
            Log.e("FavoritesApi", "Error fetching property by ID: ${e.message}")
            null
        }
    }

    override suspend fun fetchLikedProperties(userId: String): List<Property> {
        return try {
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
        } catch (e: Exception) {
            Log.e("FavoritesApi", "Error fetching liked properties: ${e.message}")
            emptyList()
        }
    }

    override suspend fun toggleLikeProperty(userId: String, propertyId: String): Boolean {
        return try {
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
                    val likeData = Likes(userId, Date()) // Domain model (Likes)
                    batch.set(likesRef, LikesEntity.fromDomainModel(likeData)) // Convert to data model
                    batch.set(likedPropertiesRef, LikesEntity.fromDomainModel(likeData)) // Convert to data model
                }
            }.await()
            true
        } catch (e: Exception) {
            Log.e("FavoritesApi", "Error toggling like: ${e.message}")
            false
        }
    }
}