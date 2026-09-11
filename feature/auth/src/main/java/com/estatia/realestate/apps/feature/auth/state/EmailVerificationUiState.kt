package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.UiState
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface EmailVerificationUiState {

@UiState
    object Idle : EmailVerificationUiState

@UiState
    object Sending : EmailVerificationUiState

@UiState
    object EmailSent : EmailVerificationUiState

@UiState
    object Checking : EmailVerificationUiState

@UiState
    object Verified : EmailVerificationUiState

@UiState
    data class Error(val message: String) : EmailVerificationUiState
}
