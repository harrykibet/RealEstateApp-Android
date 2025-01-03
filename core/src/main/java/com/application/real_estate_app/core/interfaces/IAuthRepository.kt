package com.application.real_estate_app.core.interfaces

interface IAuthRepository {
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut()
}
