package com.application.real_estate_app.core_network.db_entities

import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_model.user.UserType

data class UserEntity(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val userType: String = "TENANT",  //DEFAULT VALUE, Storing as String for FireStore compatibility
    val verified: Boolean = false,
    val likedProperties: List<String> = emptyList()
) {
    // Map to Domain Model
    fun toDomainModel() = User(
        userId = userId,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        profilePictureUrl = profilePictureUrl,
        userType = UserType.valueOf(userType), // Convert String to Enum
        verified = verified,
        likedProperties = likedProperties
    )

    companion object {
        // Map from Domain Model
        fun fromDomainModel(user: User) = UserEntity(
            userId = user.userId,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            profilePictureUrl = user.profilePictureUrl,
            userType = user.userType.name, // Convert Enum to String
            verified = user.verified,
            likedProperties = user.likedProperties
        )
    }
}
