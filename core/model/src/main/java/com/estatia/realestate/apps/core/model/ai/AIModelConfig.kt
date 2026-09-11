package com.estatia.realestate.apps.core.model.ai
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
data class AIModelConfig(
    val modelVersion: String,
    val featureThreshold: Double,
    val maxRecommendationResults: Int,
    val retrainIntervalDays: Int
)
