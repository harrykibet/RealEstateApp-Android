package com.estatia.realestate.apps.core.model.feature

import kotlinx.serialization.Serializable

@Serializable
enum class MarketCategory {
    SERVICES,
    PRODUCTS,
    PROFESSIONALS
}

@Serializable
enum class MarketServiceType {
    MAINTENANCE,
    CLEANING,
    MOVING,
    CONSTRUCTION,
    UTILITIES,
    SECURITY,
    MANAGEMENT,
    INSPECTION
}

@Serializable
data class MarketItem(
    val id: String,
    val title: String,
    val description: String,
    val category: MarketCategory,
    val type: String, // Specific type like "Plumber", "Sofa", etc.
    val price: Double? = null,
    val priceUnit: String? = null,
    val imageUrl: String? = null,
    val rating: Float? = null,
    val reviewCount: Int = 0,
    val provider: MarketProvider? = null,
    val isVerified: Boolean = false
)

@Serializable
data class MarketProvider(
    val id: String,
    val name: String,
    val profilePictureUrl: String? = null,
    val verificationLevel: String = "UNVERIFIED", // UNVERIFIED, IDENTITY, BUSINESS, PROFESSIONAL, TRUSTED
    val location: String? = null,
    val bio: String? = null
)

@Serializable
data class MarketProject(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val location: String,
    val budget: Double? = null,
    val status: String = "OPEN" // OPEN, CLOSED, IN_PROGRESS
)
