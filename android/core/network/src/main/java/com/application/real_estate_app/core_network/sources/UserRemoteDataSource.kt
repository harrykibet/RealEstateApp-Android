package com.application.real_estate_app.core_network.sources

import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_network.interfaces.IUserRemoteDataSource
import com.application.real_estate_app.core_network.db_names.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val logger: LoggerInterface,
    private val network: INetworkHandler
) : IUserRemoteDataSource {

    override suspend fun getUserById(userId: String): User? {
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
                val user = snapshot.toObject(User::class.java)

                logger.d("${this::class.simpleName}: Successfully fetched user for ID: $userId")
                user
            },
            onFailure = { e ->
                logger.e("${this::class.simpleName}: Error fetching user by ID $userId - ${e.message ?: "Unknown error"}")
                null
            }
        )
    }
}
