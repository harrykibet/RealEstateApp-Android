package com.estatia.realestate.apps.feature.auth.state

sealed interface ForgotPasswordUiState {

    /** Initial idle state */
    data class Idle(
        val email: String = ""
    ) : ForgotPasswordUiState

    /** Sending reset email */
    data class Loading(
        val email: String
    ) : ForgotPasswordUiState

    /** Email successfully sent */
    data class Success(
        val email: String
    ) : ForgotPasswordUiState

    /** Something went wrong */
    data class Error(
        val email: String,
        val message: String
    ) : ForgotPasswordUiState
}
