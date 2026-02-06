package com.estatia.realestate.apps.core.model.auth

data class AuthUser(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean
)
