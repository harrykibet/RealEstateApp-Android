package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface PhoneVerificationState {
@Helper
    data object Idle : PhoneVerificationState
    data class CodeSent(val verificationId: String) : PhoneVerificationState
@Helper
    data object Verified : PhoneVerificationState
    data class Error(
        val error: AuthException,
        val message: String? = null
    ) : PhoneVerificationState
}
