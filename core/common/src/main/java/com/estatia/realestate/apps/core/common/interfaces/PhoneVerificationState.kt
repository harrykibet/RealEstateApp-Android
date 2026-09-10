package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface PhoneVerificationState {
@Helper
    data object Idle : PhoneVerificationState
@Helper
    data class CodeSent(val verificationId: String) : PhoneVerificationState
@Helper
    data object Verified : PhoneVerificationState
@Helper
    data class Error(
        val error: AuthException,
        val message: String? = null
    ) : PhoneVerificationState
}
