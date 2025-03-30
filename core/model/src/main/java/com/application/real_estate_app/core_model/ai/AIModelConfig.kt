package com.application.real_estate_app.core_model.ai

data class AIModelConfig(
    val modelVersion: String,
    val featureThreshold: Double,
    val maxRecommendationResults: Int,
    val retrainIntervalDays: Int
)
