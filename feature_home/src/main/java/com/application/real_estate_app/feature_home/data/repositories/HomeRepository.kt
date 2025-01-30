package com.application.real_estate_app.feature_home.data.repositories


import com.application.real_estate_app.core.data.db_entities.LikesEntity
import com.application.real_estate_app.core.data.db_entities.PropertyEntity
import com.application.real_estate_app.core.data.mappers.toDomainModel
import com.application.real_estate_app.core.domain.models.Likes
import com.application.real_estate_app.core.domain.models.Property
import com.application.real_estate_app.core.data.db_names.FirestoreCollections
import com.application.real_estate_app.core.data.db_names.FirestoreFields
import com.application.real_estate_app.core.common.errors.ErrorMessages
import com.application.real_estate_app.core.domain.interfaces.INetworkHandler
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.feature_home.domain.interfaces.IHomeRepo
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val network: INetworkHandler // Injected via DI
): IHomeRepo {

    override suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property? {
        return network.safeApiCallSuspend(
            apiCall = {
            val doc = db.collection(FirestoreCollections.PROPERTIES).document(propertyId).get().await()
            doc.toObject(PropertyEntity::class.java)?.toDomainModel()
        },
            onFailure ={ exception ->
            onFailure(exception)
                log(exception.message)
        })
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
                    val likeData = Likes(userId, Date()) // Domain model (Likes)
                    batch.set(likesRef, LikesEntity.fromDomainModel(likeData)) // Convert to data model
                    batch.set(likedPropertiesRef, LikesEntity.fromDomainModel(likeData)) // Convert to data model
                }
            }.await()
            true
        },
            onFailure = { exception ->
            onFailure(exception)
                log(exception.message)
        }) ?: false
    }

    override suspend fun fetchLikedProperties(userId: String, onFailure: (Exception) -> Unit): List<Property> {
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
        }) ?: emptyList()
    }

    private fun log(message: String?){
        logger.error("${ErrorMessages.HOME_API} : $message")
    }
}
