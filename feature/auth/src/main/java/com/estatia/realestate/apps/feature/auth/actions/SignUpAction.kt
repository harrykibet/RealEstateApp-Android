package com.estatia.realestate.apps.feature.auth.actions

sealed interface SignUpAction {
    data class UserNameChanged(val value: String) : SignUpAction
    data class EmailChanged(val value: String) : SignUpAction
    data class PhoneChanged(val value: String) : SignUpAction
    data class PasswordChanged(val value: String) : SignUpAction
    data class UserTypeChanged(val value: String) : SignUpAction
    data object Submit : SignUpAction
}