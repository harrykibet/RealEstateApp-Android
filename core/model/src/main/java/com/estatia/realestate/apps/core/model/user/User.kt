package com.estatia.realestate.apps.core.model.user

data class User(
    val userId: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val profilePictureUrl: String?,
    val userType: UserType,
    val verified: Boolean,
    val likedProperties: List<String>
)
