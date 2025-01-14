package com.application.real_estate_app.feature_auth.data.apis

import android.util.Log
import com.application.real_estate_app.core.data_utils.data_models.User
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthApi @Inject constructor(
    private val db: FirebaseFirestore, //Injected via DI
    private val firebaseAuth: FirebaseAuth // Injected via DI
) : IAuthApi {
    override fun createUserIfNotExists(userId: String?, user: User) {
        val userRef = db.collection("users").document(userId!!)

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

    override fun signInWithEmail(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override fun signUpWithEmail(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getFirebaseAuth(): FirebaseAuth {
        return firebaseAuth
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }

    override suspend fun sendPasswordResetEmail(email: String): Void {
        return firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }
}