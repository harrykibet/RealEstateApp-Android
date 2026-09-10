package com.estatia.realestate.apps.core.model.user
import com.estatia.realestate.apps.core.architecture.annotations.UiState

@UiState
data class UserInteraction(
    val propertyId: String,
    val action: String, // e.g., Viewed, Liked, Saved
    val timestamp: Long // Unix timestamp
)
