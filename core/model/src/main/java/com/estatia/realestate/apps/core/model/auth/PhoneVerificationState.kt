package com.estatia.realestate.apps.core.model.auth

import com.estatia.realestate.apps.core.domain.exceptions.NetworkException


sealed interface PhoneVerificationState {
    data object Idle : PhoneVerificationState
    data class CodeSent(val verificationId: String) : PhoneVerificationState
    data object Verified : PhoneVerificationState
    data class Error(
        val error: NetworkException,
        val message: String? = null
    ) : PhoneVerificationState
}
