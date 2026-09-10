package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
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

@Helper
    data class Error(val message: String) : EmailVerificationUiState
}
