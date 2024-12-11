package com.application.real_estate_app.feature_auth.interfaces

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface AuthService {
    fun signInWithEmail(email: String, password: String): Task<AuthResult>
    fun signUpWithEmail(email: String, password: String): Task<AuthResult>
    fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuth(): FirebaseAuth
    suspend fun sendPasswordResetEmail(email: String): Void
}
