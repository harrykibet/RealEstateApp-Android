package com.application.real_estate_app.feature_home.data.apis

import android.net.ConnectivityManager
import android.util.Log
import com.application.real_estate_app.core.data_utils.db_entities.LikesEntity
import com.application.real_estate_app.core.data_utils.db_entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.data_models.Likes
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.network_utils.NetworkHandler.safeApiCallSuspend
import com.application.real_estate_app.feature_home.domain.interfaces.IHomeApi
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class HomeApi @Inject constructor(
    private val db: FirebaseFirestore,   // Injected via DI
    private val connectivityManager: ConnectivityManager // Injected via DI
): IHomeApi {

    override suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property? {
        return safeApiCallSuspend(connectivityManager = connectivityManager,
            action = {
            val doc = db.collection(FirestoreCollections.PROPERTIES).document(propertyId).get().await()
            doc.toObject(PropertyEntity::class.java)?.toDomainModel()
        },
            onFailure ={ exception ->
            onFailure(exception)
            Log.e("HomeApi", "Network error: ${exception.message}")
        })
    }

    // Updated implementation of fetchPropertiesPaginated
    override suspend fun fetchPropertiesPaginated(
        lastVisible: String?,  // Use String instead of DocumentSnapshot
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<Property>, String?> {
        return safeApiCallSuspend(connectivityManager = connectivityManager,
            action = {
            val query = if (lastVisible == null) {
                db.collection(FirestoreCollections.PROPERTIES)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
            } else {
                db.collection(FirestoreCollections.PROPERTIES)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
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
            Log.e("HomeApi", "Network error: ${exception.message}")
        }) ?: Pair(emptyList(), null)
    }

    override suspend fun toggleLikeProperty(userId: String, propertyId: String, onFailure: (Exception) -> Unit): Boolean {
        return safeApiCallSuspend(connectivityManager = connectivityManager,
            action = {
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
            Log.e("HomeApi", "Network error: ${exception.message}")
        }) ?: false
    }

    override suspend fun fetchLikedProperties(userId: String, onFailure: (Exception) -> Unit): List<Property> {
        return safeApiCallSuspend(connectivityManager = connectivityManager,
            action = {
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
            Log.e("HomeApi", "Network error: ${exception.message}")
        }) ?: emptyList()
    }
}
