package com.estatia.realestate.apps.feature.auth.state

sealed interface EmailVerificationUiState {

    object Idle : EmailVerificationUiState

    object Sending : EmailVerificationUiState

    object EmailSent : EmailVerificationUiState

    object Checking : EmailVerificationUiState

    object Verified : EmailVerificationUiState

    data class Error(val message: String) : EmailVerificationUiState
}
