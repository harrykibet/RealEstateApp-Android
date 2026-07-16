package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException

sealed interface PhoneVerificationState {
    data object Idle : PhoneVerificationState
    data class CodeSent(val verificationId: String) : PhoneVerificationState
    data object Verified : PhoneVerificationState
    data class Error(
        val error: AuthException,
        val message: String? = null
    ) : PhoneVerificationState
}