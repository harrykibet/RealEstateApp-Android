package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Utility

@Serializable
@Utility
data object CommentsBaseRoute

@Serializable
@Utility
data class CommentsRoute(val propertyId: String)
