package com.estatia.realestate.apps.core.model.ai

data class AIModelConfig(
    val modelVersion: String,
    val featureThreshold: Double,
    val maxRecommendationResults: Int,
    val retrainIntervalDays: Int
)
