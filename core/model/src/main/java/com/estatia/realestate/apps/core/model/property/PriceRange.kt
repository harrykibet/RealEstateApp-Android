package com.estatia.realestate.apps.core.model.property
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
data class PriceRange(
    val min: Double,
    val max: Double
)
