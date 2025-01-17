package com.application.real_estate_app.feature_auth.data.apis

import android.net.ConnectivityManager
import com.application.real_estate_app.core.data_utils.data_models.User
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.errors.ErrorMessages
import com.application.real_estate_app.core.logs_utils.Logger
import com.application.real_estate_app.core.network_utils.NetworkHandler.safeApiCall
import com.application.real_estate_app.feature_auth.domain.interfaces.IAuthApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
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
        safeApiCall(connectivityManager = connectivityManager,
            apiCall = {
            val userRef = db.collection(FirestoreCollections.USERS).document(userId!!)
            userRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    userRef.set(user).addOnSuccessListener {
                        // User successfully created
                    }.addOnFailureListener { exception ->
                        onFailure(exception)
                        log(exception.message)
                    }
                }
            }.addOnFailureListener { exception ->
                onFailure(exception)
                log(exception.message)
            }
        },
            onFailure = { exception ->
            onFailure(exception)
                log(exception.message)
        })
    }

    override fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return safeApiCall(connectivityManager = connectivityManager,
            apiCall = {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener{ exception ->
                    onFailure(exception)
                    log(exception.message)
                } },
            onFailure = { exception ->
            onFailure(exception)
            log(exception.message)
        })
    }

    override fun signUpWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return safeApiCall(connectivityManager = connectivityManager,
            apiCall = {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener{ exception ->
                    onFailure(exception)
                    log(exception.message)
                }},
            onFailure = { exception ->
            onFailure(exception)
            log(exception.message)
        })
    }

    override fun signOut(
        onFailure: (Exception) -> Unit) {
        safeApiCall(connectivityManager = connectivityManager,
            apiCall = {
            firebaseAuth.signOut()
        },
            onFailure = { exception ->
            onFailure(exception)
                log(exception.message)
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
        return safeApiCall(connectivityManager = connectivityManager,
            apiCall = {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnFailureListener{ exception ->
                onFailure(exception)
                log(exception.message)
            }},
            onFailure = { exception ->
            onFailure(exception)
            log(exception.message)
        })
    }

    override fun sendPasswordResetEmail(
        email: String,
        onFailure: (Exception) -> Unit): Task<Void>? {
        return safeApiCall(connectivityManager = connectivityManager,
            apiCall = {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                        log(exception.message)
                    }
            },
            onFailure = { exception ->
                onFailure(exception)
                log(exception.message)
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

    private fun log(message: String?) {
        Logger.error("${ErrorMessages.AUTH_API}: $message")
    }
}
