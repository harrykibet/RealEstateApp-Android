package com.application.real_estate_app.core_network.sources

import com.application.real_estate_app.core_common.errors.Errors
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_network.interfaces.IAuthRemoteDataSource
import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_network.db_names.FirestoreCollections
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val firebaseAuth: FirebaseAuth, // Injected via DI
    private val network: INetworkHandler  // Injected via DI
) : IAuthRemoteDataSource {

    override fun createUserIfNotExists(
        userId: String?,
        user: User,
        onFailure: (Exception) -> Unit, ) {
        network.safeApiCall(
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
        return network.safeApiCall(
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
        return network.safeApiCall(
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
        network.safeApiCall(
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
        return network.safeApiCall(
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
        return network.safeApiCall(
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
        logger.e("${Errors.AUTH_REPO}: $message")
    }
}
