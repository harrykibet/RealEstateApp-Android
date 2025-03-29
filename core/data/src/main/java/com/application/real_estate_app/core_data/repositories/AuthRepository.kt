package com.application.real_estate_app.core_data.repositories

import com.application.real_estate_app.core_data.interfaces.IAuthRepository
import com.application.real_estate_app.core_model.User
import com.application.real_estate_app.core_network.interfaces.IAuthRemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource
) : IAuthRepository {

    override fun createUserIfNotExists(
        userId: String?,
        user: User,
        onFailure: (Exception) -> Unit, ) {
        return remoteDataSource.createUserIfNotExists(userId, user, onFailure)
    }

    override fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return remoteDataSource.signInWithEmail(email, password, onFailure)
    }

    override fun signUpWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit): Task<AuthResult>? {
        return remoteDataSource.signUpWithEmail(email, password, onFailure)
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

    override fun sendPasswordResetEmail(
        email: String,
        onFailure: (Exception) -> Unit): Task<Void>? {
        return remoteDataSource.sendPasswordResetEmail(email, onFailure)
    }

    override fun isUserAuthenticated(): Boolean {
        return remoteDataSource.isUserAuthenticated()
    }

    override fun getCurrentUserId(): String? {
        return remoteDataSource.getCurrentUserId()
    }

    override fun getCurrentUserEmail(): String? {
        return remoteDataSource.getCurrentUserEmail()
    }
}
