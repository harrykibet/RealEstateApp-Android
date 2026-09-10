package com.estatia.realestate.apps.feature.auth.actions
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface SignUpAction {
@Helper
    data class UserNameChanged(val value: String) : SignUpAction
@Helper
    data class EmailChanged(val value: String) : SignUpAction
@Helper
    data class PhoneChanged(val value: String) : SignUpAction
@Helper
    data class PasswordChanged(val value: String) : SignUpAction
@Helper
    data class UserTypeChanged(val value: String) : SignUpAction
@Helper
    data object Submit : SignUpAction
}
