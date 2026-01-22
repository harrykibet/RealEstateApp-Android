package com.estatia.realestate.apps.feature.auth.state

sealed interface PhoneVerificationUiState {

    data class Countdown(
        val secondsLeft: Int
    ) : PhoneVerificationUiState

    object Verifying : PhoneVerificationUiState

    object Expired : PhoneVerificationUiState

    object Success : PhoneVerificationUiState

    data class Error(
        val message: String
    ) : PhoneVerificationUiState
}
