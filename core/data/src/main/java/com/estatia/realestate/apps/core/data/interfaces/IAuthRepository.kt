package com.estatia.realestate.apps.core.data.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun isUserAuthenticated(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?

    fun getCurrentUser(): AuthUserDomainModel?
    suspend fun sendPasswordResetEmail(email: String): AppResult<Unit>

    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): AppResult<AuthUserDomainModel>

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AppResult<AuthUserDomainModel>

    suspend fun signInWithGoogle(
        idToken: String
    ): AppResult<AuthUserDomainModel>

    suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserDomainModel
    ): AppResult<Unit>

    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneVerificationState>

    suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): AppResult<Unit>

    suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): AppResult<String>

    suspend fun sendEmailVerification(): AppResult<Unit>
    suspend fun isEmailVerified(): AppResult<Boolean>
    suspend fun signOut(): AppResult<Unit>
}
