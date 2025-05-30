package com.estatia.realestate.apps.core.model.user

import com.estatia.realestate.apps.core.model.property.PriceRange

data class UserPreferences(
    val preferredPropertyTypes: List<String>, // e.g., Apartment, House, Studio
    val preferredPriceRange: PriceRange,
    val preferredLocations: List<String> // e.g., Nairobi, Mombasa
)
