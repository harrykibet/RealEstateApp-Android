package com.application.real_estate_app.feature_auth.domain.interfaces

import com.application.real_estate_app.core.data_utils.data_models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface IAuthApi {
    fun createUserIfNotExists(userId: String?, user: User)
    fun signInWithEmail(email: String, password: String): Task<AuthResult>
    fun signUpWithEmail(email: String, password: String): Task<AuthResult>
    fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuth(): FirebaseAuth
    suspend fun sendPasswordResetEmail(email: String): Void
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
}