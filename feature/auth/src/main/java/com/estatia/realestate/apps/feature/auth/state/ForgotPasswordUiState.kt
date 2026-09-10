package com.estatia.realestate.apps.feature.auth.state
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface ForgotPasswordUiState {

    /** Initial idle state */
@Helper
    data class Idle(
        val email: String = ""
    ) : ForgotPasswordUiState

    /** Sending reset email */
@Helper
    data class Loading(
        val email: String
    ) : ForgotPasswordUiState

    /** Email successfully sent */
@Helper
    data class Success(
        val email: String
    ) : ForgotPasswordUiState

    /** Something went wrong */
@Helper
    data class Error(
        val email: String,
        val message: String
    ) : ForgotPasswordUiState
}
