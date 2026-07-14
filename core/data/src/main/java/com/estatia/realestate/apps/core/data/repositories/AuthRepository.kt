package com.estatia.realestate.apps.core.data.repositories

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.mappers.auth.AuthUserMapper
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestoreUserProfileMapper
import com.estatia.realestate.apps.core.model.auth.AuthUser
import com.estatia.realestate.apps.core.model.auth.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource
) : IAuthRepository {

    override suspend fun createUserIfNotExists(
        userId: String,
        user: UserDomainModel
    ): Result<Unit> {
        val remoteUser = FirestoreUserProfileMapper.toEntity(user)
        return remoteDataSource.createOrUpdateUserProfile(userId, remoteUser)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthUser> {
        return remoteDataSource.signUpWithEmail(email, password)
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<AuthUser> {
        return remoteDataSource.signInWithEmail(email, password)
    }

    override suspend fun signOut(): Result<Unit> {
        return remoteDataSource.signOut()
    }

    override fun getCurrentUser(): AuthUser {
        return AuthUserMapper.fromFirebase(remoteDataSource.getCurrentUser())
    }

    override suspend fun signInWithGoogle(
        idToken: String
    ): Result<AuthUser> {
        return remoteDataSource.signInWithGoogle(idToken)
    }

    override suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> {
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
        activity: Activity
    ): Flow<PhoneVerificationState> {
        return remoteDataSource.startPhoneNumberVerification(phoneNumber, activity)
    }

    override suspend fun verifyPhoneCode(
        verificationId: String,
        code: String
    ): Result<Unit> {
        return remoteDataSource.verifyPhoneCode(verificationId, code)
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity
    ): Result<String> {
        return remoteDataSource.resendVerificationCode(phoneNumber, activity)
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return remoteDataSource.sendEmailVerification()
    }

    override suspend fun isEmailVerified(): Result<Boolean> {
        return remoteDataSource.isEmailVerified()
    }
}
