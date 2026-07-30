package com.estatia.realestate.apps.feature.comments.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CommentsBaseRoute

@Serializable
data class CommentsRoute(val propertyId: String)
