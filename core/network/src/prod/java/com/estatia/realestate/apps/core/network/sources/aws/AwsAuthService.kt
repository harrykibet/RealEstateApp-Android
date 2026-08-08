package com.estatia.realestate.apps.core.network.sources.aws

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * AWS implementation of [IAuthRemoteDataSource] (Skeleton).
 * This will use AWS Cognito/Amplify for authentication in the future.
 */
internal class AwsAuthService @Inject constructor() : IAuthRemoteDataSource {

    override suspend fun createOrUpdateUserProfile(userId: String, user: UserEntityModel): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun signUpWithEmail(email: String, password: String): AppResult<NetworkUserEntity> =
        AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.AuthException.Unknown(Exception("AWS Not Implemented")))

    override suspend fun signInWithEmail(email: String, password: String): AppResult<NetworkUserEntity> =
        AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.AuthException.Unknown(Exception("AWS Not Implemented")))

    override suspend fun signInWithGoogle(idToken: String): AppResult<NetworkUserEntity> =
        AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.AuthException.Unknown(Exception("AWS Not Implemented")))

    override fun startPhoneNumberVerification(phoneNumber: String, activity: Activity): Flow<PhoneVerificationState> =
        flowOf(PhoneVerificationState.Error(com.estatia.realestate.apps.core.common.exceptions.AuthException.Unknown(Exception("AWS Not Implemented"))))

    override suspend fun verifyPhoneCode(verificationId: String, code: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun resendVerificationCode(phoneNumber: String, activity: Activity): AppResult<String> =
        AppResult.Success("")

    override suspend fun sendPasswordResetEmail(email: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun sendEmailVerification(): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun isEmailVerified(): AppResult<Boolean> =
        AppResult.Success(false)

    override fun isUserAuthenticated(): Flow<Boolean> = flowOf(false)

    override fun getCurrentUser(): NetworkUserEntity? = null

    override fun getCurrentUserId(): String? = null

    override fun getCurrentUserEmail(): String? = null

    override suspend fun signOut(): AppResult<Unit> = AppResult.Success(Unit)
}
