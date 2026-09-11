package com.estatia.realestate.apps.core.network.db_entities
import com.estatia.realestate.apps.core.architecture.annotations.Data.EntityModel

@EntityModel
data class NetworkUserEntity(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean
)
