package com.estatia.realestate.apps.core.domain.security

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level repository for user authentication and authorization.
 */
interface IAuthRepository {
    /**
     * Returns a [Flow] that emits true if the user is authenticated.
     */
    fun isUserAuthenticated(): Flow<Boolean>

    /**
     * Returns the current user's ID.
     */
    fun getCurrentUserId(): String?

    /**
     * Returns the current user's email.
     */
    fun getCurrentUserEmail(): String?

    /**
     * Returns the current [AuthUserDomainModel].
     */
    fun getCurrentUser(): AuthUserDomainModel?

    /**
     * Triggers a password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): AppResult<Unit>

    /**
     * Registers a new user.
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): AppResult<AuthUserDomainModel>

    /**
     * Signs in an existing user.
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AppResult<AuthUserDomainModel>

    /**
     * Signs in using Google.
     */
    suspend fun signInWithGoogle(
        idToken: String
    ): AppResult<AuthUserDomainModel>

    /**
     * Updates the user's public profile data.
     */
    suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserDomainModel
    ): AppResult<Unit>

    /**
     * Starts the phone verification flow.
     */
    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneVerificationState>

    /**
     * Verifies the phone code.
     */
    suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): AppResult<Unit>

    /**
     * Resends the phone verification code.
     */
    suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): AppResult<String>

    /**
     * Sends an email verification link.
     */
    suspend fun sendEmailVerification(): AppResult<Unit>

    /**
     * Checks if the user's email is verified.
     */
    suspend fun isEmailVerified(): AppResult<Boolean>

    /**
     * Signs the user out.
     */
    suspend fun signOut(): AppResult<Unit>
}
