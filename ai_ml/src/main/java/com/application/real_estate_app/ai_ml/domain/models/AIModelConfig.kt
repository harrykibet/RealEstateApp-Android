package com.application.real_estate_app.ai_ml.domain.models

data class AIModelConfig(
    val modelVersion: String,
    val featureThreshold: Double,
    val maxRecommendationResults: Int,
    val retrainIntervalDays: Int
)
