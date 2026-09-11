package com.estatia.realestate.apps.feature.auth.state

import com.estatia.realestate.apps.core.architecture.annotations.Data
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface AuthState {

    /** App just launched / auth check in progress */
    @Data.AuthState
    data object Loading : AuthState

    /** App is idle */
    @Data.AuthState
    data object Idle : AuthState

    /** No authenticated Firebase user */
    @Data.AuthState
    data object Unauthenticated : AuthState

    /** Firebase user exists but email is not verified */
    @Data.AuthState
    data class EmailVerificationRequired(
        val email: String
    ) : AuthState

    /** Email verified but phone number missing */
    @com.estatia.realestate.apps.core.architecture.annotations.Data.AuthState
    data class PhoneVerificationRequired(
        val phoneNumber: String?
    ) : AuthState

    /** Fully authenticated and verified */
    @com.estatia.realestate.apps.core.architecture.annotations.Data.AuthState
    data class Authenticated(
        val user: AuthUserDomainModel
    ) : AuthState

    /** Any unrecoverable auth error */
    @com.estatia.realestate.apps.core.architecture.annotations.Data.AuthState
    data class Error(
        val message: String
    ) : AuthState
}
