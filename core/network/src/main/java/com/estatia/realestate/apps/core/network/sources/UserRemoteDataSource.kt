package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
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

    override suspend fun getUserById(userId: String): UserEntityModel {
        require(userId.isNotBlank()) {
            "${this::class.simpleName}: Invalid userId provided."
        }

        return requireNotNull(network.safeApiCallSuspend(
            apiCall = {
                val snapshot = firestore.collection(FirestoreCollections.USERS)
                    .document(userId)
                    .get()
                    .await()
                val user = snapshot.toObject(UserEntityModel::class.java)
                    ?: throw NoSuchElementException("User not found for ID: $userId")

                logger.d("${this::class.simpleName}: Successfully fetched user for ID: $userId")
                user
            },
            onFailure = { e ->
                logger.e("${this::class.simpleName}: Error fetching user by ID $userId - ${e.message ?: "Unknown error"}")
            }
        )) { "${this::class.simpleName}: Unable to fetch user for ID: $userId" }
    }
}
