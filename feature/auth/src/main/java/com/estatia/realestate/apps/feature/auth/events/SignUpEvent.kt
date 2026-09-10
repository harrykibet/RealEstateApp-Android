package com.estatia.realestate.apps.feature.auth.events
import com.estatia.realestate.apps.core.architecture.annotations.Helper

sealed interface SignUpEvent {
@Helper
    data class RequireEmailVerification(val email: String) : SignUpEvent
    data class RequirePhoneVerification(val phone: String) : SignUpEvent
}
