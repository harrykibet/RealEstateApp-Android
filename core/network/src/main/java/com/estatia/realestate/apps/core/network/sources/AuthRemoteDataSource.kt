package com.estatia.realestate.apps.core.network.sources

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.interfaces.INetworkHandler
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.model.user.User
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val firebaseAuth: FirebaseAuth, // Injected via DI
    private val network: INetworkHandler  // Injected via DI
) : IAuthRemoteDataSource {

    override fun createUserIfNotExists(
        userId: String?,
        user: User) {
        network.safeApiCall(
            apiCall = {
                val userRef = db.collection(FirestoreCollections.USERS).document(userId!!)
                userRef.get().addOnSuccessListener { document ->
                    if (!document.exists()) {
                        userRef.set(user).addOnSuccessListener {
                            // User successfully created
                        }.addOnFailureListener { exception ->
                            log(exception.message)
                        }
                    }
                }.addOnFailureListener { exception ->
                    log(exception.message)
                }
            },
            onFailure = { exception ->
                log(exception.message)
            })
    }

    override fun signInWithEmail(
        email: String,
        password: String): Task<AuthResult>? {
        return network.safeApiCall(
            apiCall = {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener{ exception ->
                        log(exception.message)
                    } },
            onFailure = { exception ->
                log(exception.message)
            })
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthResult> {
        return network.safeApiCallSuspend(
            apiCall = {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .await() // <-- suspends until Firebase finishes
            },
            onFailure = { exception ->
                log(exception.message)
            }
        )?.let { Result.Success(it) }
            ?: Result.Error(Exception("Failed to sign up"))
    }



    private suspend fun signInWithCredentialSuspend(
        credential: PhoneAuthCredential
    ): Unit = suspendCancellableCoroutine { cont ->
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                if (cont.isActive) cont.resume(Unit)
            }
            .addOnFailureListener { exception ->
                if (cont.isActive) cont.resumeWithException(exception)
            }
    }

    override suspend fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ): Result<Unit> {

        val result = network.safeApiCallSuspend(
            apiCall = {
                signInWithCredentialSuspend(credential)
            },
            onFailure = { /* logging already handled globally */ }
        )

        return if (result != null) {
            Result.Success(Unit)
        } else {
            Result.Error(IllegalStateException("Phone auth failed"))
        }
    }

    private suspend fun resendCodeSuspend(
        phoneNumber: String,
        activity: Activity,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ): String = suspendCancellableCoroutine { cont ->

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setForceResendingToken(resendingToken)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // We DO NOT auto-sign in here — verificationId is what we want
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    if (cont.isActive) cont.resumeWithException(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    if (cont.isActive) cont.resume(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ): Result<String> {

        val verificationId = network.safeApiCallSuspend(
            apiCall = {
                resendCodeSuspend(
                    phoneNumber = phoneNumber,
                    activity = activity,
                    resendingToken = resendingToken
                )
            },
            onFailure = { exception ->
                logger.e("AuthRepository: resend failed - ${exception.message}")
            }
        )

        return if (verificationId != null) {
            Result.Success(verificationId)
        } else {
            Result.Error(
                IllegalStateException("Failed to resend verification code")
            )
        }
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
        idToken: String): Task<AuthResult>? {
        return network.safeApiCall(
            apiCall = {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).addOnFailureListener{ exception ->
                    log(exception.message)
                }},
            onFailure = { exception ->
                log(exception.message)
            })
    }

    override suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> {
        return network.safeApiCallSuspend(
            apiCall = {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Unit
            },
            onFailure = { }
        )?.let {
            Result.Success(Unit)
        } ?: Result.Error(Exception("Failed to send reset email"))
    }


    override suspend fun sendEmailVerification(): Result<Unit> =
        try {
            val user = firebaseAuth.currentUser
                ?: return Result.Error(IllegalStateException("User not logged in"))

            user.sendEmailVerification().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }


    override suspend fun isEmailVerified(): Result<Boolean> =
        network.safeApiCallSuspend(
            apiCall = {
                firebaseAuth.currentUser
                    ?.reload()
                    ?.await()

                firebaseAuth.currentUser?.isEmailVerified == true
            },
            onFailure = { }
        )?.let {
            Result.Success(it)
        } ?: Result.Error(Exception("Failed to check verification status"))



    override fun isUserAuthenticated(): Flow<Boolean> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }

        firebaseAuth.addAuthStateListener(authListener)

        // Emit initial value
        trySend(firebaseAuth.currentUser != null)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authListener)
        }
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
