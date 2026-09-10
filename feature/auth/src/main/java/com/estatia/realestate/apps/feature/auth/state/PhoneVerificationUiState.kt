package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface PhoneVerificationUiState {

@Helper
    object Idle : PhoneVerificationUiState

@Helper
    object SendingCode : PhoneVerificationUiState

@Helper
    data class CodeSent(val verificationId: String) : PhoneVerificationUiState

    data class Countdown(
        val secondsLeft: Int
    ) : PhoneVerificationUiState

@Helper
    object Verifying : PhoneVerificationUiState

@Helper
    object Expired : PhoneVerificationUiState

@Helper
    object Success : PhoneVerificationUiState

@Helper
    data class Error(
        val message: String
    ) : PhoneVerificationUiState
}
