package com.estatia.realestate.apps.feature.auth.state

sealed interface ForgotPasswordAction {
    data class EmailChanged(val value: String) : ForgotPasswordAction
    data object Submit : ForgotPasswordAction
    data object Retry : ForgotPasswordAction
}
