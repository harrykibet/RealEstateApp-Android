package com.estatia.realestate.apps.core.model.property

data class PropertyRecommendation(
    val recommendedProperties: List<Property>, // List of recommended properties
    val recommendationScore: Double, // A score indicating the confidence level of recommendation
    val timestamp: Long // Timestamp of the recommendation
)
