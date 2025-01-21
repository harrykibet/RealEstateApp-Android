package com.application.real_estate_app.feature_favorites.data.apis

import com.application.real_estate_app.core.data_utils.db_entities.LikesEntity
import com.application.real_estate_app.core.data_utils.db_entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.data_models.Likes
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.errors.ErrorMessages
import com.application.real_estate_app.core.interfaces.INetworkHandler
import com.application.real_estate_app.core.interfaces.LoggerInterface
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFavoritesApi
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FavoritesApi @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val network: INetworkHandler // Injected via DI
): IFavoritesApi {

    override suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property? {
        return network.safeApiCallSuspend(
            apiCall = {
            val doc = db.collection(FirestoreCollections.PROPERTIES).document(propertyId).get().await()
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

    private fun log(message: String?) {
        logger.error("${ErrorMessages.FAVORITES_API} : $message")
    }
}