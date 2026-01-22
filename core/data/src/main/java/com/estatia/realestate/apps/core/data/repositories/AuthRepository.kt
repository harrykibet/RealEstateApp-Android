package com.estatia.realestate.apps.core.data.repositories

import android.app.Activity
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.model.user.User
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource
) : IAuthRepository {

    override fun createUserIfNotExists(
        userId: String?,
        user: User) {
        return remoteDataSource.createUserIfNotExists(userId, user)
    }

    override fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return remoteDataSource.signInWithEmail(email, password, onFailure)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<AuthResult> {
        return remoteDataSource.signUpWithEmail(email, password)
    }

    override fun signOut(
        onFailure: (Exception) -> Unit) {
        return remoteDataSource.signOut(onFailure)
    }

    override fun getFirebaseAuth(): FirebaseAuth {
        return remoteDataSource.getFirebaseAuth()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return remoteDataSource.getCurrentUser()
    }

    override fun firebaseAuthWithGoogle(
        idToken: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return remoteDataSource.firebaseAuthWithGoogle(idToken, onFailure)
    }

   override suspend fun sendPasswordResetEmail(
       email: String): Result<Unit> {
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

    override suspend fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ): Result<Unit> {
        return remoteDataSource.signInWithPhoneAuthCredential(credential)
    }

    override suspend fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ): Result<String> {
        return remoteDataSource.resendVerificationCode(phoneNumber, activity, resendingToken)
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return remoteDataSource.sendEmailVerification()
    }

    override suspend fun isEmailVerified(): Result<Boolean> {
        return remoteDataSource.isEmailVerified()
    }
}
