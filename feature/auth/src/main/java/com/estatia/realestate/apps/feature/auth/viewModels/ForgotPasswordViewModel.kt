package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.feature.auth.state.ForgotPasswordAction
import com.estatia.realestate.apps.feature.auth.state.ForgotPasswordUiState
import com.estatia.realestate.apps.core.common.errors.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ForgotPasswordUiState>(
            ForgotPasswordUiState.Idle()
        )

    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.EmailChanged ->
                updateEmail(action.value)

            ForgotPasswordAction.Submit ->
                submit()

            ForgotPasswordAction.Retry ->
                retry()
        }
    }

    private fun updateEmail(email: String) {
        val newState = when (val current = _uiState.value) {
            is ForgotPasswordUiState.Idle -> current.copy(email = email)
            is ForgotPasswordUiState.Error -> current.copy(email = email)
            else -> current
        }
        _uiState.value = newState
    }

    private fun submit() {
        val email = when (val current = _uiState.value) {
            is ForgotPasswordUiState.Idle -> current.email
            is ForgotPasswordUiState.Error -> current.email
            else -> return
        }

        if (email.isBlank()) {
            _uiState.value =
                ForgotPasswordUiState.Error(
                    email = email,
                    message = "Email cannot be empty"
                )
            return
        }

        viewModelScope.launch(ioDispatcher) {
            _uiState.value = ForgotPasswordUiState.Loading(email)

            when (val result =
                authRepository.sendPasswordResetEmail(email)) {

                is Result.Success -> {
                    _uiState.value =
                        ForgotPasswordUiState.Success(email)
                }

                is Result.Error -> {
                    _uiState.value =
                        ForgotPasswordUiState.Error(
                            email = email,
                            message = result.exception.message
                                ?: "Failed to send reset email"
                        )
                }
            }
        }
    }

    private fun retry() {
        val email = when (val state = _uiState.value) {
            is ForgotPasswordUiState.Error -> state.email
            else -> return
        }

        _uiState.value = ForgotPasswordUiState.Idle(email)
    }
}
