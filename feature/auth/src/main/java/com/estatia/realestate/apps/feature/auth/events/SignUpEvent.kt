package com.estatia.realestate.apps.feature.auth.events
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiEvent
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface SignUpEvent {
@UiEvent
    data class RequireEmailVerification(val email: String) : SignUpEvent
@UiEvent
    data class RequirePhoneVerification(val phone: String) : SignUpEvent
}
