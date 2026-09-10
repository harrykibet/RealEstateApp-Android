package com.estatia.realestate.apps.feature.auth.events
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface SignUpEvent {
@Helper
    data class RequireEmailVerification(val email: String) : SignUpEvent
@Helper
    data class RequirePhoneVerification(val phone: String) : SignUpEvent
}
