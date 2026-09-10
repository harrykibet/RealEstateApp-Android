package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Serializable
@Helper
data class PropertyDetailRoute(val propertyId: String)

@Serializable
data object PropertyRoute

@Serializable
@Helper
data object PropertyMediaCaptureRoute

@Serializable
data object PropertyBaseRoute
