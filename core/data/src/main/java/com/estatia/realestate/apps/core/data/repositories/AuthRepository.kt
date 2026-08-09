package com.estatia.realestate.apps.core.data.repositories

import android.app.Activity
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.mappers.auth.NetworkUserMapper
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestoreUserProfileMapper
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource,
) : IAuthRepository {

    override suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserDomainModel,
    ): AppResult<Unit> {
        val firestoreUser = FirestoreUserProfileMapper.toEntity(user)
        return remoteDataSource.createOrUpdateUserProfile(userId, firestoreUser)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
    ): AppResult<AuthUserDomainModel> {
        return remoteDataSource.signUpWithEmail(email, password)
            .map { networkUser ->
            NetworkUserMapper.fromEntity(networkUser)
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AppResult<AuthUserDomainModel> {
        return remoteDataSource.signInWithEmail(email, password)
            .map { networkUser ->
            NetworkUserMapper.fromEntity(networkUser)
        }
    }

    override suspend fun signOut(): AppResult<Unit> {
        return remoteDataSource.signOut()
    }

    override fun getCurrentUser(): AuthUserDomainModel? {
        return remoteDataSource
            .getCurrentUser()
            ?.let { networkUser ->
                NetworkUserMapper.fromEntity(networkUser)
            }
    }

    override suspend fun signInWithGoogle(
        idToken: String,
    ): AppResult<AuthUserDomainModel> {
        return remoteDataSource
            .signInWithGoogle(idToken)
            .map { networkUser ->
                NetworkUserMapper.fromEntity(networkUser)
            }
    }

    override suspend fun sendPasswordResetEmail(
        email: String,
    ): AppResult<Unit> {
        return remoteDataSource.sendPasswordResetEmail(email)
    }

    override fun isUserAuthenticated(): Flow<Boolean> {
        return remoteDataSource.isUserAuthenticated()
    }

    override fun getCurrentUserId(): String? {
        return remoteDataSource.getCurrentUserId()
    }

    override fun getCurrentUserEmail(): String? {
        return remoteDataSource.getCurrentUserEmail()
    }

    override fun startPhoneNumberVerification(
        phoneNumber: String,
        activity: Activity,
    ): Flow<PhoneVerificationState> {
        return remoteDataSource.startPhoneNumberVerification(phoneNumber, activity)
    }

    override suspend fun verifyPhoneCode(
        verificationId: String,
        code: String,
    ): AppResult<Unit> {
        return remoteDataSource.verifyPhoneCode(verificationId, code)
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
    ): AppResult<String> {
        return remoteDataSource.resendVerificationCode(phoneNumber, activity)
    }

    override suspend fun sendEmailVerification(): AppResult<Unit> {
        return remoteDataSource.sendEmailVerification()
    }

    override suspend fun isEmailVerified(): AppResult<Boolean> {
        return remoteDataSource.isEmailVerified()
    }
}
