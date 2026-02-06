package com.estatia.realestate.apps.core.model.auth

sealed interface PhoneVerificationState {
    data object Idle : PhoneVerificationState
    data class CodeSent(val verificationId: String) : PhoneVerificationState
    data object Verified : PhoneVerificationState
    data class Error(val message: String) : PhoneVerificationState
}
