package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface EmailVerificationUiState {

@Helper
    object Idle : EmailVerificationUiState

@Helper
    object Sending : EmailVerificationUiState

@Helper
    object EmailSent : EmailVerificationUiState

@Helper
    object Checking : EmailVerificationUiState

@Helper
    object Verified : EmailVerificationUiState

    data class Error(val message: String) : EmailVerificationUiState
}
