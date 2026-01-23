package com.estatia.realestate.apps.core.network.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.model.user.User
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

interface IAuthRemoteDataSource {
    fun isUserAuthenticated(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut(onFailure: (Exception) -> Unit)

    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuth(): FirebaseAuth

    fun firebaseAuthWithGoogle(
        idToken: String
    ): Task<AuthResult>?

    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthResult>

    fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun createUserIfNotExists(
        userId: String?,
        user: User
    )

    suspend fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ): Result<Unit>

    suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ): Result<String> // returns new verificationId

    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Result<Boolean>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
