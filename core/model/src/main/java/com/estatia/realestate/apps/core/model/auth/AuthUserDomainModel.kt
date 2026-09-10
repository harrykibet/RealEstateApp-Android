package com.estatia.realestate.apps.core.model.auth
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class AuthUserDomainModel(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean
)
