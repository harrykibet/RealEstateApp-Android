package com.application.real_estate_app.core_model

data class UserPreferences(
    val preferredPropertyTypes: List<String>, // e.g., Apartment, House, Studio
    val preferredPriceRange: PriceRange,
    val preferredLocations: List<String> // e.g., Nairobi, Mombasa
)
