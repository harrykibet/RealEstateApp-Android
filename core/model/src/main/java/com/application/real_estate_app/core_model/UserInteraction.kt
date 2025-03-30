package com.application.real_estate_app.core_model

data class UserInteraction(
    val propertyId: String,
    val action: String, // e.g., Viewed, Liked, Saved
    val timestamp: Long // Unix timestamp
)