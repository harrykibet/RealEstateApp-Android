package com.application.real_estate_app.domain.interfaces

interface AuthRepository {
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut()
}
