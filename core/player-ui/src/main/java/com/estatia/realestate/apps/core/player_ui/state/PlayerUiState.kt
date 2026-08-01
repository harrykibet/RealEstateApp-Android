package com.estatia.realestate.apps.core.player_ui.state

sealed class PlayerUiState {
    object Idle : PlayerUiState()
    object Buffering : PlayerUiState()
    object Playing : PlayerUiState()
    object Paused : PlayerUiState()
    object Ended : PlayerUiState()
    object Ready : PlayerUiState()

    data class Error(val message: String?) : PlayerUiState()
}
