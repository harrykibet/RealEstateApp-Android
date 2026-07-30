package com.estatia.realestate.apps.feature.home.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute // route to Home screen

@Serializable
data object HomeBaseRoute // route to base navigation graph

@Serializable
data class PropertyDetailRoute(val propertyId: String) // route to property detail screen
