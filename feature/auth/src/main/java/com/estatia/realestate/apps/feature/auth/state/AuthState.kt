package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.model.user.User

sealed interface AuthState {

    /** App just launched / auth check in progress */
    data object Loading : AuthState

    /** App is idle */
    data object Idle : AuthState

    /** No authenticated Firebase user */
    data object Unauthenticated : AuthState

    /** Firebase user exists but email is not verified */
    data class EmailVerificationRequired(
        val email: String
    ) : AuthState

    /** Email verified but phone number missing */
    data class PhoneVerificationRequired(
        val phoneNumber: String?
    ) : AuthState

    /** Fully authenticated and verified */
    data class Authenticated(
        val user: User
    ) : AuthState

    /** Any unrecoverable auth error */
    data class Error(
        val message: String
    ) : AuthState
}

