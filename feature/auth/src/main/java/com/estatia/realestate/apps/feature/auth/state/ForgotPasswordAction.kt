package com.estatia.realestate.apps.feature.auth.state
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface ForgotPasswordAction {
@Helper
    data class EmailChanged(val value: String) : ForgotPasswordAction
@Helper
    data object Submit : ForgotPasswordAction
@Helper
    data object Retry : ForgotPasswordAction
}
