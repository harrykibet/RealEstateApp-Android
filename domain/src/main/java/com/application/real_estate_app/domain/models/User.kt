package com.application.real_estate_app.domain.models

enum class UserType(val displayName: String) {
    TENANT("Tenant"),
    PROPERTY_OWNER("Property Owner");

    companion object {
        fun fromDisplayName(displayName: String): UserType? {
            return entries.find { it.displayName == displayName }
        }
    }
}


data class User(
    val userId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val profilePictureUrl: String,
    val userType: UserType,
    val verified: Boolean,
    val likedProperties: List<String>
)
