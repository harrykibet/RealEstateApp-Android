package com.estatia.realestate.apps.core.model.user

data class UserClassificationResult(
    val userId: String,
    val category: UserCategory,
    val confidenceScore: Double
)


