package com.estatia.realestate.apps.core.model.user
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
data class UserFeatures(
    val totalInteractions: Int,
    val avgPricePreference: Double,
    val preferredLocationScore: Int
)
