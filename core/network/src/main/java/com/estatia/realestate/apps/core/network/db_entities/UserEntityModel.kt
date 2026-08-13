package com.estatia.realestate.apps.core.network.db_entities

data class UserEntityModel(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val userType: String = "TENANT",  //DEFAULT VALUE, Storing as String for FireStore compatibility
    val verificationLevel: String = "NONE",
    val likedProperties: List<String> = emptyList(),
    val propertyCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0
)
