package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.UiState
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface PhoneVerificationUiState {

@UiState
    object Idle : PhoneVerificationUiState

@UiState
    object SendingCode : PhoneVerificationUiState

@UiState
    data class CodeSent(val verificationId: String) : PhoneVerificationUiState

@UiState
    data class Countdown(
        val secondsLeft: Int
    ) : PhoneVerificationUiState

@UiState
    object Verifying : PhoneVerificationUiState

@UiState
    object Expired : PhoneVerificationUiState

@UiState
    object Success : PhoneVerificationUiState

@UiState
    data class Error(
        val message: String
    ) : PhoneVerificationUiState
}
