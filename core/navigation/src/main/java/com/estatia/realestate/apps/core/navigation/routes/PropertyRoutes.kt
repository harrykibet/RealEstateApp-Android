package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Utility

@Serializable
@Utility
data class PropertyDetailRoute(val propertyId: String)

@Serializable
@Utility
data object PropertyRoute

@Serializable
@Utility
data object PropertyMediaCaptureRoute

@Serializable
@Utility
data object PropertyBaseRoute
