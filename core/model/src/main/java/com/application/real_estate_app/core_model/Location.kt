package com.application.real_estate_app.core_model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
