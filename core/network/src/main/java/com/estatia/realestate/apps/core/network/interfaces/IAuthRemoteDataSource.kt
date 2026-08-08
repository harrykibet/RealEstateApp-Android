package com.estatia.realestate.apps.core.network.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import kotlinx.coroutines.flow.Flow

/**
 * Remote data source for handling user authentication and profile management.
 */
interface IAuthRemoteDataSource {
    /**
     * Returns a [Flow] that emits the current authentication status.
     */
    fun isUserAuthenticated(): Flow<Boolean>

    /**
     * Returns the unique ID of the currently logged-in user, or null if none.
     */
    fun getCurrentUserId(): String?

    /**
     * Returns the email of the currently logged-in user, or null if none.
     */
    fun getCurrentUserEmail(): String?

    /**
     * Returns the current [NetworkUserEntity] if available.
     */
    fun getCurrentUser(): NetworkUserEntity?

    /**
     * Signs up a new user with email and password.
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): AppResult<NetworkUserEntity>

    /**
     * Signs in an existing user with email and password.
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AppResult<NetworkUserEntity>

    /**
     * Signs in a user using an interactive browser-based flow (e.g., OIDC/OAuth2).
     * Typically used for providers like AWS Cognito or Auth0.
     */
    suspend fun signInInteractive(
        activity: Activity
    ): AppResult<NetworkUserEntity>

    /**
     * Signs in a user using a Google ID token.
     */
    suspend fun signInWithGoogle(
        idToken: String
    ): AppResult<NetworkUserEntity>

    /**
     * Creates or updates the detailed user profile in the database.
     */
    suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserEntityModel
    ): AppResult<Unit>

    /**
     * Initiates the phone number verification process.
     */
    fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity
    ): Flow<PhoneVerificationState>

    /**
     * Verifies the SMS code sent to the user's phone.
     */
    suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): AppResult<Unit>

    /**
     * Resends the SMS verification code.
     * @return New verification ID.
     */
    suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): AppResult<String>

    /**
     * Sends a verification email to the current user.
     */
    suspend fun sendEmailVerification(): AppResult<Unit>

    /**
     * Checks if the current user's email has been verified.
     */
    suspend fun isEmailVerified(): AppResult<Boolean>

    /**
     * Sends a password reset email to the specified address.
     */
    suspend fun sendPasswordResetEmail(email: String): AppResult<Unit>

    /**
     * Signs the current user out.
     */
    suspend fun signOut(): AppResult<Unit>
}
