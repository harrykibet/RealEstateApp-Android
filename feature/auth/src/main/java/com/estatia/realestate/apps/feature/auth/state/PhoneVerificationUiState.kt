package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface PhoneVerificationUiState {

@Helper
    object Idle : PhoneVerificationUiState

@Helper
    object SendingCode : PhoneVerificationUiState

@Helper
    data class CodeSent(val verificationId: String) : PhoneVerificationUiState

@Helper
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
