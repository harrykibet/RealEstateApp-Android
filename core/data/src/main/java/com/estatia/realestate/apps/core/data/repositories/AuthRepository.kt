package com.estatia.realestate.apps.core.data.repositories

import android.app.Activity
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.errors.map
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.mappers.auth.FirebaseAuthUserMapper
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestoreUserProfileMapper
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource
) : IAuthRepository {

    override suspend fun createOrUpdateUserProfile(
        userId: String,
        user: UserDomainModel
    ): Result<Unit> {
        val firestoreUser = FirestoreUserProfileMapper.toEntity(user)
        return remoteDataSource.createOrUpdateUserProfile(userId, firestoreUser)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthUserDomainModel> {
        return remoteDataSource.signUpWithEmail(email, password)
            .map { firebaseUser ->
            FirebaseAuthUserMapper.fromFirebase(firebaseUser)
        }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<AuthUserDomainModel> {
        return remoteDataSource.signInWithEmail(email, password)
            .map { firebaseUser ->
            FirebaseAuthUserMapper.fromFirebase(firebaseUser)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return remoteDataSource.signOut()
    }

    override fun getCurrentUser(): AuthUserDomainModel? {
        return remoteDataSource
            .getCurrentUser()
            ?.let { firebaseUser ->
                FirebaseAuthUserMapper.fromFirebase(firebaseUser)
            }
    }

    override suspend fun signInWithGoogle(
        idToken: String
    ): Result<AuthUserDomainModel> {
        return remoteDataSource
            .signInWithGoogle(idToken)
            .map { firebaseUser ->
                FirebaseAuthUserMapper.fromFirebase(firebaseUser)
            }
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
