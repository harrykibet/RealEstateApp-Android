package com.estatia.realestate.apps.core.data.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.auth.AuthUser
import com.estatia.realestate.apps.core.model.auth.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun isUserAuthenticated(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?

    fun getCurrentUser(): AuthUser?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

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

    suspend fun createOrUpdateUserProfile(
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
    ): Result<String>

    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Result<Boolean>
    suspend fun signOut(): Result<Unit>
}
