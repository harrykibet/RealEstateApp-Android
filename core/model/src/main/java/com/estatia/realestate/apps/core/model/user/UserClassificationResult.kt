package com.estatia.realestate.apps.core.model.user
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class UserClassificationResult(
    val userId: String,
    val category: UserCategory,
    val confidenceScore: Double
)


