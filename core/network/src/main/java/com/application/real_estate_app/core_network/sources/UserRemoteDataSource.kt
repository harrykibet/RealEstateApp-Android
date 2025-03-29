package com.application.real_estate_app.core_network.sources

import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_model.User
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_network.interfaces.IUserRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val network: INetworkHandler // Injected via DI
) : IUserRemoteDataSource {
    override suspend fun getUserInfo(userId: String) : User {
        // Fetch user data from FireStore if not cached
        firestore.collection(FirestoreCollections.USERS)
            .document(comment!!.userId!!)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    userCache[comment.userId] = user // Cache the user data
                    holder.bind(comment, user)
                } else {
                    holder.bind(comment, null) // Pass null if user data is not found
                }
            }
            .addOnFailureListener {
                holder.bind(comment, null) // Handle failure by binding with null user
            }
    }

}