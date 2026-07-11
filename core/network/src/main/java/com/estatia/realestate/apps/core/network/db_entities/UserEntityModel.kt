package com.estatia.realestate.apps.core.network.db_entities

data class UserEntityModel(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userType: String = "TENANT",  //DEFAULT VALUE, Storing as String for FireStore compatibility
    val verified: Boolean = false,
    val likedProperties: List<String> = emptyList()
)
