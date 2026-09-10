package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface AuthState {

    /** App just launched / auth check in progress */
@Helper
    data object Loading : AuthState

    /** App is idle */
@Helper
    data object Idle : AuthState

    /** No authenticated Firebase user */
@Helper
    data object Unauthenticated : AuthState

    /** Firebase user exists but email is not verified */
@Helper
    data class EmailVerificationRequired(
        val email: String
    ) : AuthState

    /** Email verified but phone number missing */
@Helper
    data class PhoneVerificationRequired(
        val phoneNumber: String?
    ) : AuthState

    /** Fully authenticated and verified */
@Helper
    data class Authenticated(
        val user: AuthUserDomainModel
    ) : AuthState

    /** Any unrecoverable auth error */
@Helper
    data class Error(
        val message: String
    ) : AuthState
}
