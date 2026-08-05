package com.estatia.realestate.apps.core.network.db_entities

data class NetworkUserEntity(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean
)
