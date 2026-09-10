package com.estatia.realestate.apps.core.model.property
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class PropertyRecommendation(
    val recommendedProperties: List<PropertyDomainModel>, // List of recommended properties
    val recommendationScore: Double, // A score indicating the confidence level of recommendation
    val timestamp: Long // Timestamp of the recommendation
)
