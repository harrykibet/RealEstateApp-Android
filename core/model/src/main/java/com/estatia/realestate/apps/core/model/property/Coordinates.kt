package com.estatia.realestate.apps.core.model.property
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
