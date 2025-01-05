package com.application.real_estate_app.core.interfaces

interface IAuthApiCore {
    fun isUserAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUserEmail(): String?
    fun signOut()
}
