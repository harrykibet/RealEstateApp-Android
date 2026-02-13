package com.estatia.realestate.apps.feature.player.state

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 1L, // avoid division by zero
)
