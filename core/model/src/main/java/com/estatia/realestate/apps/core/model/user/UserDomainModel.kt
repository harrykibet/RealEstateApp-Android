package com.estatia.realestate.apps.core.model.user

data class UserDomainModel(
    val userId: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val profilePictureUrl: String?,
    val bio: String? = null,
    val userType: UserType,
    val verified: Boolean,
    val likedProperties: List<String>,
    val propertyCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0
)
