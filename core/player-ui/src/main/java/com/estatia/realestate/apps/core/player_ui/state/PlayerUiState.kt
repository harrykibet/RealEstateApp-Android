package com.estatia.realestate.apps.core.player_ui.state

sealed class PlayerUiState {
    object Idle : PlayerUiState()
    object Buffering : PlayerUiState()
    object Reconnecting : PlayerUiState()
    object LowBandwidth : PlayerUiState()
    object Playing : PlayerUiState()
    object Paused : PlayerUiState()
    object Ended : PlayerUiState()
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
