package com.estatia.realestate.apps.core.network.interfaces

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Result
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
    ): Result<FirebaseUser>

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<FirebaseUser>

    suspend fun signInWithGoogle(
        idToken: String
    ): Result<FirebaseUser>

    suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserEntityModel
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
    suspend fun signOut(): Result<Unit>
}
