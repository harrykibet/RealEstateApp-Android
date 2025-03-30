package com.application.real_estate_app.core_model

data class PropertyRecommendation(
    val recommendedProperties: List<Property>, // List of recommended properties
    val recommendationScore: Double, // A score indicating the confidence level of recommendation
    val timestamp: Long // Timestamp of the recommendation
)
