package com.estatia.realestate.apps.feature.auth.state

sealed interface AuthState {
    object Unauthenticated : AuthState
    object PhoneVerificationRequired : AuthState
    object EmailVerificationRequired : AuthState
    object Authenticated : AuthState
}
