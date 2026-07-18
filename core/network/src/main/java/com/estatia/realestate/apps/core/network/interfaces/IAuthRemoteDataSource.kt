package com.estatia.realestate.apps.core.network.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface IAuthRemoteDataSource {
    fun isUserAuthenticated(): Flow<Boolean>
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?

    fun getCurrentUser(): FirebaseUser?
    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): AppResult<FirebaseUser>

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): AppResult<FirebaseUser>

    suspend fun signInWithGoogle(
        idToken: String
    ): AppResult<FirebaseUser>

    suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserEntityModel
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
    ): AppResult<String> // returns new verificationId

    suspend fun sendEmailVerification(): AppResult<Unit>
    suspend fun isEmailVerified(): AppResult<Boolean>
    suspend fun sendPasswordResetEmail(email: String): AppResult<Unit>
    suspend fun signOut(): AppResult<Unit>
}
