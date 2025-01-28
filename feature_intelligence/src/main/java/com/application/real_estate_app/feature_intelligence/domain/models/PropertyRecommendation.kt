package com.application.real_estate_app.feature_intelligence.domain.models

data class PropertyRecommendation(
    val recommendedProperties: List<Property>, // List of recommended properties
    val recommendationScore: Double, // A score indicating the confidence level of recommendation
    val timestamp: Long // Timestamp of the recommendation
)

data class Property(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val location: String,
    val imageUrl: String
)
