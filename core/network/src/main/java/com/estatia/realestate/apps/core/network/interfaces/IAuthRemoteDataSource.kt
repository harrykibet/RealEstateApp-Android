package com.estatia.realestate.apps.core.network.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.auth.AuthUser
import com.estatia.realestate.apps.core.model.auth.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import kotlinx.coroutines.flow.Flow

interface IAuthRemoteDataSource {
    fun isUserAuthenticated(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut(onFailure: (Exception) -> Unit)

    fun getCurrentUser(): AuthUser?
    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthUser>

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<AuthUser>

    suspend fun signInWithGoogle(
        idToken: String
    ): Result<AuthUser>

    suspend fun createUserIfNotExists(
        userId: String,
        user: UserDomainModel
    ): Result<Unit>


    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneVerificationState>

    suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): Result<Unit>

    suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Result<String> // returns new verificationId

    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Result<Boolean>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun signOut(): Result<Unit>
}
