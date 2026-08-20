package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
data class PropertyDetailRoute(val propertyId: String)

@Serializable
data object PropertyRoute

@Serializable
data object PropertyMediaCaptureRoute

@Serializable
data object PropertyBaseRoute
