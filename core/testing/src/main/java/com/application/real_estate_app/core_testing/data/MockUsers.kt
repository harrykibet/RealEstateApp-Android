package com.application.real_estate_app.core_testing.data

import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_model.user.UserType

val mockUsers = listOf(
    User(
        userId = "user_001",
        name = "Alice Wanjiku",
        email = "alice@example.com",
        phoneNumber = "+254712345678",
        profilePictureUrl = "https://randomuser.me/api/portraits/women/1.jpg",
        userType = UserType.TENANT,
        verified = true,
        likedProperties = listOf("property_101", "property_102")
    ),
    User(
        userId = "user_002",
        name = "Brian Otieno",
        email = "brian@example.com",
        phoneNumber = "+254798765432",
        profilePictureUrl = "https://randomuser.me/api/portraits/men/2.jpg",
        userType = UserType.PROPERTY_OWNER,
        verified = false,
        likedProperties = listOf("property_103")
    ),
    User(
        userId = "user_003",
        name = "Clara Mwende",
        email = "clara@example.com",
        phoneNumber = "+254701234567",
        profilePictureUrl = "https://randomuser.me/api/portraits/women/3.jpg",
        userType = UserType.AGENT,
        verified = true,
        likedProperties = emptyList()
    )
)
