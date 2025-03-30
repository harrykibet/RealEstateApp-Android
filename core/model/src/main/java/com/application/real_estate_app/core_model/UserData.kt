package com.application.real_estate_app.core_model

data class UserData(
    val userId: String,
    val preferences: UserPreferences,
    val pastInteractions: List<UserInteraction>
)





