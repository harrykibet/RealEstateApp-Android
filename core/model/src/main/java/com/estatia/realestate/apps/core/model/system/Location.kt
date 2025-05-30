package com.estatia.realestate.apps.core.model.system

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
