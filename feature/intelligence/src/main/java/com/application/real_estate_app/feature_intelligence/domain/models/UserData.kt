package com.application.real_estate_app.feature_intelligence.domain.models

data class UserData(
    val userId: String,
    val preferences: UserPreferences,
    val pastInteractions: List<UserInteraction>
)

data class UserPreferences(
    val preferredPropertyTypes: List<String>, // e.g., Apartment, House, Studio
    val preferredPriceRange: PriceRange,
    val preferredLocations: List<String> // e.g., Nairobi, Mombasa
)

data class UserInteraction(
    val propertyId: String,
    val action: String, // e.g., Viewed, Liked, Saved
    val timestamp: Long // Unix timestamp
)

data class PriceRange(
    val min: Double,
    val max: Double
)
