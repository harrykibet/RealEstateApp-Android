package com.application.real_estate_app.core_model.user

data class UserClassificationResult(
    val userId: String,
    val category: UserCategory,
    val confidenceScore: Double
)


