package com.application.real_estate_app.feature_auth.repositories

import com.application.real_estate_app.domain.models.User
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import android.util.Log
import com.application.real_estate_app.domain.interfaces.IUserRepository

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : IUserRepository {

    override fun createUserIfNotExists(userId: String, user: User) {
        //TODO("Add more relevant methods such as updating and deleting a user info ")
        val userRef = firestore.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) return@addOnSuccessListener

            // Create new user
            userRef.set(user).addOnSuccessListener {
                // User successfully created
            }.addOnFailureListener { exception ->
                // Handle error
                Log.e("UserRepository", "User creation error ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            // Handle error
            Log.e("UserRepository", "UnKnown error ${exception.message}")
        }
    }
}
