package com.estatia.realestate.apps.core.network.sources

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.core.RetryConfigs
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IAuthExceptionMapper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthService @Inject constructor(
    private val database: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val authErrorMapper: IAuthExceptionMapper,
    private val networkClient: INetworkClient
) : IAuthRemoteDataSource {


    private val resendToken =
        AtomicReference<PhoneAuthProvider.ForceResendingToken?>()


    override suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserEntityModel
    ): AppResult<Unit> {

        return networkClient.execute(RetryConfigs.AUTH) {

            val userRef =
                database.collection(FirestoreCollections.USERS)
                    .document(userId)

            userRef.set(
                user,
                SetOptions.merge()
            ).await()
        }
    }


    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): AppResult<FirebaseUser> {


        return networkClient.execute {

            val firebaseUser =
                firebaseAuth
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()
                    .user
                    ?: throw AuthException.SignUpFailed


            firebaseUser
        }
    }


    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): AppResult<FirebaseUser> {


        return networkClient.execute {

            val firebaseUser =
                firebaseAuth
                    .signInWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()
                    .user
                    ?: throw AuthException.SignInFailed


            firebaseUser
        }
    }


    override suspend fun signInWithGoogle(
        idToken: String
    ): AppResult<FirebaseUser> {


        return networkClient.execute {


            val credential =
                GoogleAuthProvider
                    .getCredential(
                        idToken,
                        null
                    )


            val firebaseUser =
                firebaseAuth
                    .signInWithCredential(
                        credential
                    )
                    .await()
                    .user
                    ?: throw AuthException.SignInFailed


            firebaseUser
        }
    }

    override fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneVerificationState> = callbackFlow {
        trySend(PhoneVerificationState.Idle)

        val verificationActive = AtomicBoolean(true)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                if (!verificationActive.get())
                    return


                resendToken.set(token)

                trySend(
                    PhoneVerificationState.CodeSent(
                        verificationId
                    )
                )
            }

            override fun onVerificationCompleted(
                credential: PhoneAuthCredential
            ) {

                launch {

                    if (!verificationActive.get())
                        return@launch


                    try {

                        signInWithCredentialSuspend(
                            credential
                        )

                        if (verificationActive.get()) {
                            trySend(
                                PhoneVerificationState.Verified
                            )
                        }


                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {

                        if (verificationActive.get()) {

                            trySend(
                                PhoneVerificationState.Error(
                                    authErrorMapper.map(e)
                                )
                            )
                        }
                    }
                }
            }

            override fun onVerificationFailed(
                e: FirebaseException
            ) {

                if (!verificationActive.get())
                    return


                trySend(
                    PhoneVerificationState.Error(
                        authErrorMapper.map(e)
                    )
                )
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose()
        {

            verificationActive.set(false)
            resendToken.set(null)

        }
    }


    override suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): AppResult<Unit> {


        val credential =
            PhoneAuthProvider.getCredential(
                verificationId,
                code
            )


        return networkClient.execute {

            signInWithCredentialSuspend(
                credential
            )
        }
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


    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): AppResult<String> {


        return networkClient.execute {

            resendCodeSuspend(
                phoneNumber,
                activity
            )
        }
    }

    private suspend fun resendCodeSuspend(
        phoneNumber: String,
        activity: Activity
    ): String = suspendCancellableCoroutine { cont ->

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setForceResendingToken(
                resendToken.get()
                    ?: throw AuthException.TokenError(
                        "Missing resend token"
                    )
            )
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
                    resendToken.set(token)
                    if (cont.isActive) cont.resume(verificationId)
                }
            })
            .build()

        try {

            PhoneAuthProvider.verifyPhoneNumber(options)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {

            if (cont.isActive) {
                cont.resumeWithException(e)
            }
        }
    }


    override suspend fun sendPasswordResetEmail(
        email: String
    ): AppResult<Unit> {


        return networkClient.execute(RetryConfigs.AUTH) {


            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()
        }
    }


    override suspend fun sendEmailVerification(): AppResult<Unit> {


        return networkClient.execute(RetryConfigs.AUTH) {


            val user =
                firebaseAuth.currentUser
                    ?: throw AuthException.UserNotAuthenticated

            user.sendEmailVerification()
                .await()
        }
    }


    override suspend fun isEmailVerified(): AppResult<Boolean> {


        return networkClient.execute {

            val user =
                firebaseAuth.currentUser
                    ?: throw AuthException.UserNotAuthenticated


            user.reload().await()

            user.isEmailVerified
        }
    }

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

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override suspend fun signOut(): AppResult<Unit> {

        firebaseAuth.signOut()

        return AppResult.Success(Unit)
    }
}
