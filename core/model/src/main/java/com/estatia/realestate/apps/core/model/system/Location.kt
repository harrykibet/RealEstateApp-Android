package com.estatia.realestate.apps.core.model.system
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
