package com.estatia.realestate.apps.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserLocation(
    val country: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)
