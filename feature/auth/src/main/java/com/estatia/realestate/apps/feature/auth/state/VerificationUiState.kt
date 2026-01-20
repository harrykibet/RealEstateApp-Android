package com.estatia.realestate.apps.feature.auth.state

sealed interface VerificationUiState {

    data class Countdown(
        val secondsLeft: Int
    ) : VerificationUiState

    object Verifying : VerificationUiState

    object Expired : VerificationUiState

    object Success : VerificationUiState

    data class Error(
        val message: String
    ) : VerificationUiState
}
