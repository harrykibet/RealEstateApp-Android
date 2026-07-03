package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.db_entities.UserEntity
import com.estatia.realestate.apps.core.network.interfaces.INetworkHandler
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val logger: LoggerInterface,
    private val network: INetworkHandler
) : IUserRemoteDataSource {

    override suspend fun getUserById(userId: String): UserEntity? {
        if (userId.isBlank()) {
            logger.e("${this::class.simpleName}: Invalid userId provided.")
            return null
        }

        return network.safeApiCallSuspend(
            apiCall = {
                val snapshot = firestore.collection(FirestoreCollections.USERS)
                    .document(userId)
                    .get()
                    .await()
                val user = snapshot.toObject(UserEntity::class.java)

                logger.d("${this::class.simpleName}: Successfully fetched user for ID: $userId")
                user
            },
            onFailure = { e ->
                logger.e("${this::class.simpleName}: Error fetching user by ID $userId - ${e.message ?: "Unknown error"}")
            }
        )
    }
}
