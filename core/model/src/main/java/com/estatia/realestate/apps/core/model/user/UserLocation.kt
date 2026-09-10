package com.estatia.realestate.apps.core.model.user

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@Serializable
@DomainModel
data class UserLocation(
    val country: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)
