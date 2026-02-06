package com.estatia.realestate.apps.core.network.sources

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.model.auth.AuthUser
import com.estatia.realestate.apps.core.model.auth.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.User
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkHandler
import com.google.firebase.FirebaseException
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
import kotlinx.coroutines.launch
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
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    override suspend fun createUserIfNotExists(
        userId: String,
        user: User
    ): Result<Unit> {
        return try {
            val userRef = db
                .collection(FirestoreCollections.USERS)
                .document(userId)

            val snapshot = userRef.get().await()

            if (!snapshot.exists()) {
                userRef.set(user).await()
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }



    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthUser> {
        return network.safeApiCallSuspend(
            apiCall = {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .await()
                    .user
            },
            onFailure = { exception ->
                log(exception.message)
            }
        )?.let { user ->
            Result.Success(user.toAuthUser())
        } ?: Result.Error(Exception("Failed to sign up"))
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<AuthUser> {
        return network.safeApiCallSuspend(
            apiCall = {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .await()
                    .user
            },
            onFailure = { exception ->
                log(exception.message)
            }
        )?.let { user ->
            Result.Success(user.toAuthUser())
        } ?: Result.Error(Exception("Failed to sign in"))
    }

    override suspend fun signInWithGoogle(
        idToken: String
    ): Result<AuthUser> {
        return network.safeApiCallSuspend(
            apiCall = {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .await()
                    .user
            },
            onFailure = { exception ->
                log(exception.message)
            }
        )?.let { user ->
            Result.Success(user.toAuthUser())
        } ?: Result.Error(Exception("Failed to sign in with Google"))
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

    override fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneVerificationState> = callbackFlow {
        trySend(PhoneVerificationState.Idle)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                resendingToken = token
                trySend(PhoneVerificationState.CodeSent(verificationId))
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                launch {
                    try {
                        signInWithCredentialSuspend(credential)
                        trySend(PhoneVerificationState.Verified)
                    } catch (e: Exception) {
                        trySend(
                            PhoneVerificationState.Error(
                                e.message ?: "Verification failed"
                            )
                        )
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(PhoneVerificationState.Error(e.message ?: "Verification failed"))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose { }
    }

    override suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): Result<Unit> {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)

        val result = network.safeApiCallSuspend(
            apiCall = {
                signInWithCredentialSuspend(credential)
            },
            onFailure = { }
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
                    resendingToken = token
                    if (cont.isActive) cont.resume(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Result<String> {
        val token = resendingToken
            ?: return Result.Error(IllegalStateException("No resending token available"))

        val verificationId = network.safeApiCallSuspend(
            apiCall = {
                resendCodeSuspend(
                    phoneNumber = phoneNumber,
                    activity = activity,
                    resendingToken = token
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

    override fun getCurrentUser(): AuthUser? {
        return firebaseAuth.currentUser?.toAuthUser()
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

    private fun FirebaseUser.toAuthUser(): AuthUser {
        return AuthUser(
            userId = uid,
            displayName = displayName,
            email = email,
            phoneNumber = phoneNumber,
            photoUrl = photoUrl?.toString(),
            isEmailVerified = isEmailVerified
        )
    }

    private fun log(message: String?) {
        logger.e("${Errors.AUTH_REPO}: $message")
    }
}
