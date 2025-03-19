package com.application.real_estate_app.core.domain.models

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
