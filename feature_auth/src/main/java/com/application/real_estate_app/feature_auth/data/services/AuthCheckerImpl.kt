package com.application.real_estate_app.feature_auth.data.services

import com.application.real_estate_app.domain.interfaces.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthCheckerImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
