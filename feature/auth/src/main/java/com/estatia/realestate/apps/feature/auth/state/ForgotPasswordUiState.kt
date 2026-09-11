package com.estatia.realestate.apps.feature.auth.state
import com.estatia.realestate.apps.core.architecture.annotations.UiState
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface ForgotPasswordUiState {

    /** Initial idle state */
@UiState
    data class Idle(
        val email: String = ""
    ) : ForgotPasswordUiState

    /** Sending reset email */
@UiState
    data class Loading(
        val email: String
    ) : ForgotPasswordUiState

    /** Email successfully sent */
@UiState
    data class Success(
        val email: String
    ) : ForgotPasswordUiState

    /** Something went wrong */
@UiState
    data class Error(
        val email: String,
        val message: String
    ) : ForgotPasswordUiState
}
