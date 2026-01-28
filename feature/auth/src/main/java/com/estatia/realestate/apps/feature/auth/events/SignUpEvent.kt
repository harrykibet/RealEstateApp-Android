package com.estatia.realestate.apps.feature.auth.events

sealed interface SignUpEvent {
    data class RequireEmailVerification(val email: String) : SignUpEvent
    data class RequirePhoneVerification(val phone: String) : SignUpEvent
}
