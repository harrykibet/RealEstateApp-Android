package com.estatia.realestate.apps.core.network.sources

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.model.auth.AuthUser
import com.estatia.realestate.apps.core.model.auth.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.IApiExecutor
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
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
    private val db: FirebaseFirestore,
    private val logger: LoggerInterface,
    private val firebaseAuth: FirebaseAuth,
    private val apiExecutor: IApiExecutor
) : IAuthRemoteDataSource {


    private var resendingToken:
            PhoneAuthProvider.ForceResendingToken? = null


    override suspend fun createUserIfNotExists(
        userId: String,
        user: UserDomainModel
    ): Result<Unit> {

        return apiExecutor.execute {

            val userRef =
                db.collection(FirestoreCollections.USERS)
                    .document(userId)


            val snapshot =
                userRef.get().await()


            if (!snapshot.exists()) {
                userRef.set(user).await()
            }

            Unit
        }
    }


    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthUser> {


        return apiExecutor.execute {

            val firebaseUser =
                firebaseAuth
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()
                    .user
                    ?: throw IllegalStateException(
                        "Firebase returned null user"
                    )


            firebaseUser.toAuthUser()
        }
    }


    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<AuthUser> {


        return apiExecutor.execute {

            val firebaseUser =
                firebaseAuth
                    .signInWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()
                    .user
                    ?: throw IllegalStateException(
                        "Firebase returned null user"
                    )


            firebaseUser.toAuthUser()
        }
    }


    override suspend fun signInWithGoogle(
        idToken: String
    ): Result<AuthUser> {


        return apiExecutor.execute {


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
                    ?: throw IllegalStateException(
                        "Google login failed"
                    )


            firebaseUser.toAuthUser()
        }
    }


    override suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): Result<Unit> {


        val credential =
            PhoneAuthProvider.getCredential(
                verificationId,
                code
            )


        return apiExecutor.execute {

            signInWithCredentialSuspend(
                credential
            )

            Unit
        }
    }


    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Result<String> {


        return apiExecutor.execute {

            resendCodeSuspend(
                phoneNumber,
                activity
            )
        }
    }


    override suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> {


        return apiExecutor.execute {


            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()


            Unit
        }
    }


    override suspend fun sendEmailVerification(): Result<Unit> {


        return apiExecutor.execute {


            val user =
                firebaseAuth.currentUser
                    ?: throw IllegalStateException(
                        "User not logged in"
                    )


            user.sendEmailVerification()
                .await()


            Unit
        }
    }


    override suspend fun isEmailVerified(): Result<Boolean> {


        return apiExecutor.execute {


            firebaseAuth.currentUser
                ?.reload()
                ?.await()


            firebaseAuth.currentUser
                ?.isEmailVerified == true
        }
    }


    override fun signOut(): Result<Unit> {


        return apiExecutor.execute {

            firebaseAuth.signOut()

            Unit
        }
    }
}