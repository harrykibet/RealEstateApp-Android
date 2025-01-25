package com.application.real_estate_app.core.domain.interfaces

interface AuthApiInterface {
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut(onFailure: (Exception) -> Unit)
}
