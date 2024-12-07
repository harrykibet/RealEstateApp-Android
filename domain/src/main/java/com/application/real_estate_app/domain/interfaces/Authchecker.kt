package com.application.real_estate_app.domain.interfaces

interface AuthChecker {
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut()
}
