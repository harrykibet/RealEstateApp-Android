package com.estatia.realestate.apps.core.model.user

data class UserInteraction(
    val propertyId: String,
    val action: String, // e.g., Viewed, Liked, Saved
    val timestamp: Long // Unix timestamp
)