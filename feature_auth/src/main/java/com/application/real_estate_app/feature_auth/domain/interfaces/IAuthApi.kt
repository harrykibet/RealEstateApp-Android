package com.application.real_estate_app.feature_auth.domain.interfaces

import com.application.real_estate_app.core.data_utils.data_models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface IAuthApi {

    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuth(): FirebaseAuth
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?

    fun sendPasswordResetEmail(
        email: String,
        onFailure: (Exception) -> Unit
    ): Task<Void>?

    fun firebaseAuthWithGoogle(
        idToken: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun signOut(onFailure: (Exception) -> Unit)
    fun signUpWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun signInWithEmail(
        email: String,
        password: String,
        onFailure: (Exception) -> Unit
    ): Task<AuthResult>?

    fun createUserIfNotExists(
        userId: String?,
        user: User,
        onFailure: (Exception) -> Unit
    )
}