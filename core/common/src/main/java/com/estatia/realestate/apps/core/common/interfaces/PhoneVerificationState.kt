package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.architecture.annotations.AuthState
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface PhoneVerificationState {
    @AuthState
    data object Idle : PhoneVerificationState
    @AuthState
    data class CodeSent(val verificationId: String) : PhoneVerificationState
    @AuthState
    data object Verified : PhoneVerificationState
    @AuthState
    data class Error(
        val error: AuthException,
        val message: String? = null
    ) : PhoneVerificationState
}
