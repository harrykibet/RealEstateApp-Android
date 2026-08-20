package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
data object CommentsBaseRoute

@Serializable
data class CommentsRoute(val propertyId: String)
