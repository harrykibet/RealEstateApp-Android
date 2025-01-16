package com.application.real_estate_app.feature_auth.data.apis

import android.net.ConnectivityManager
import android.util.Log
import com.application.real_estate_app.core.data_utils.data_models.User
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.network_utils.NetworkHandler
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
    private val db: FirebaseFirestore, // Injected via DI
    private val firebaseAuth: FirebaseAuth, // Injected via DI
    private val connectivityManager: ConnectivityManager // Injected via DI
) : IAuthApi {

    override fun createUserIfNotExists(
        userId: String?,
        user: User,
        onFailure: (Exception) -> Unit, ) {
        NetworkHandler.safeApiCall(connectivityManager = connectivityManager,
            action = {
            val userRef = db.collection(FirestoreCollections.USERS).document(userId!!)
            userRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    userRef.set(user).addOnSuccessListener {
                        // User successfully created
                    }.addOnFailureListener { exception ->
                        Log.e("AuthApi", "User creation error: ${exception.message}")
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("AuthApi", "Unknown error: ${exception.message}")
            }
        },
            onFailure = { exception ->
            onFailure(exception)
            Log.e("AuthApi", "Network error: ${exception.message}")
        })
    }

    override fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return NetworkHandler.safeApiCall(connectivityManager = connectivityManager,
            action = {
            firebaseAuth.signInWithEmailAndPassword(email, password)
        },
            onFailure = { exception ->
            onFailure(exception)
            Log.e("AuthApi", "Network error: ${exception.message}")
        })
    }

    override fun signUpWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return NetworkHandler.safeApiCall(connectivityManager = connectivityManager,
            action = {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
        },
            onFailure = { exception ->
            onFailure(exception)
            Log.e("AuthApi", "Network error: ${exception.message}")
        })
    }

    override fun signOut(
        onFailure: (Exception) -> Unit) {
        NetworkHandler.safeApiCall(connectivityManager = connectivityManager,
            action = {
            firebaseAuth.signOut()
        },
            onFailure = { exception ->
            onFailure(exception)
            Log.e("AuthApi", "Network error: ${exception.message}")
        })
    }

    override fun getFirebaseAuth(): FirebaseAuth {
        return firebaseAuth
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun firebaseAuthWithGoogle(
        idToken: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return NetworkHandler.safeApiCall(connectivityManager = connectivityManager,
            action = {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
        },
            onFailure = { exception ->
            onFailure(exception)
            Log.e("AuthApi", "Network error: ${exception.message}")
        })
    }

    override suspend fun sendPasswordResetEmail(
        email: String,
        onFailure: (Exception) -> Unit): Void? {
        return NetworkHandler.safeApiCallSuspend(connectivityManager = connectivityManager,
            action = {
            firebaseAuth.sendPasswordResetEmail(email).await()
        },
            onFailure = { exception ->
            onFailure(exception)
            Log.e("AuthApi", "Network error: ${exception.message}")
        })
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
