package com.estatia.realestate.apps.core.player_ui

import androidx.compose.runtime.Composable

data class InteractiveOverlayNode(
    val id: String,
    val content: @Composable () -> Unit
)
