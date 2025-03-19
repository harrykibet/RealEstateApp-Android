package com.application.real_estate_app.feature_intelligence.domain.models

data class AIModelConfig(
    val modelVersion: String,
    val featureThreshold: Double,
    val maxRecommendationResults: Int,
    val retrainIntervalDays: Int
)
