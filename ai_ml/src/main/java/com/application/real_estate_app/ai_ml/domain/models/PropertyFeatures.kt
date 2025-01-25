package com.application.real_estate_app.ai_ml.domain.models

data class PropertyFeatures(
    val pricePerSquareMeter: Double,
    val locationPopularityScore: Int,
    val amenitiesCount: Int
)

data class UserFeatures(
    val totalInteractions: Int,
    val avgPricePreference: Double,
    val preferredLocationScore: Int
)
