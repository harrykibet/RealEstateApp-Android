package com.estatia.realestate.apps.feature.auth.state
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiAction
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface ForgotPasswordAction {
@UiAction
    data class EmailChanged(val value: String) : ForgotPasswordAction
@UiAction
    data object Submit : ForgotPasswordAction
@UiAction
    data object Retry : ForgotPasswordAction
}
