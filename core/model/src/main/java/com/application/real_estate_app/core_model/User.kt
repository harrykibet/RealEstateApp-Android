package com.application.real_estate_app.core_model

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
