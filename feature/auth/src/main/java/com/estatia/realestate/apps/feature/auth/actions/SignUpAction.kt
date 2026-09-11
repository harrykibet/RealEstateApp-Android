package com.estatia.realestate.apps.feature.auth.actions
import com.estatia.realestate.apps.core.architecture.annotations.UiAction
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface SignUpAction {
@UiAction
    data class UserNameChanged(val value: String) : SignUpAction
@UiAction
    data class EmailChanged(val value: String) : SignUpAction
@UiAction
    data class PhoneChanged(val value: String) : SignUpAction
@UiAction
    data class PasswordChanged(val value: String) : SignUpAction
@UiAction
    data class UserTypeChanged(val value: String) : SignUpAction
@UiAction
    data object Submit : SignUpAction
}
