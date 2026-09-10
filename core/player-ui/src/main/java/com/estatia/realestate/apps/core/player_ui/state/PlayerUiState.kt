package com.estatia.realestate.apps.core.player_ui.state

import com.estatia.realestate.apps.core.architecture.annotations.UiState

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@UiState
sealed class PlayerUiState {
@Helper
    object Idle : PlayerUiState()
@Helper
    object Buffering : PlayerUiState()
@Helper
    object Reconnecting : PlayerUiState()
@Helper
    object LowBandwidth : PlayerUiState()
@Helper
    object Playing : PlayerUiState()
@Helper
    object Paused : PlayerUiState()
@Helper
    object Ended : PlayerUiState()
@Helper
    object Ready : PlayerUiState()

    data class Error(
        val message: String?,
        val type: PlayerErrorType = PlayerErrorType.UNKNOWN
    ) : PlayerUiState()
}

enum class PlayerErrorType {
    NETWORK,
    SERVER,
    NOT_FOUND,
    DECODER,
    INVALID_URI,
    WATCHDOG,
    UNKNOWN
}
