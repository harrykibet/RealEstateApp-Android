package com.estatia.realestate.apps.core.data.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.model.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.firebase.auth.PhoneAuthProvider

interface IAuthRepository {
    fun isUserAuthenticated(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut(onFailure: (Exception) -> Unit)

    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuth(): FirebaseAuth

    fun sendPasswordResetEmail(
        email: String,
        onFailure: (Exception) -> Unit
    ): Task<Void>?

    fun firebaseAuthWithGoogle(
        idToken: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun signUpWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun createUserIfNotExists(
        userId: String?,
        user: User,
        onFailure: (Exception) -> Unit
    )

    suspend fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ): Result<Unit>

    suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ): Result<String> // returns new verificationId

}
