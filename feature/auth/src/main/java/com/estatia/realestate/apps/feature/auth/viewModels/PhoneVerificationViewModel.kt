package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.feature.auth.state.PhoneVerificationUiState
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.estatia.realestate.apps.core.common.interfaces.PhoneVerificationState

@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: IAuthRepository,
    @param:Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    // navController.currentBackStackEntry
    //    ?.savedStateHandle
    //    ?.set("phoneNumber", phoneNumber)
    //
    //navController.navigate(PHONE_VERIFICATION)
    val phoneNumber: String = checkNotNull(savedStateHandle["phoneNumber"])

    private val _uiState =
        MutableStateFlow<PhoneVerificationUiState>(
            PhoneVerificationUiState.Countdown(120)
        )
    val uiState: StateFlow<PhoneVerificationUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (second in 120 downTo 1) {
                _uiState.value = PhoneVerificationUiState.Countdown(second)
                delay(1_000)
            }
            _uiState.value = PhoneVerificationUiState.Expired
        }
    }

    fun startPhoneNumberVerification(activity: Activity) {
        _uiState.value = PhoneVerificationUiState.SendingCode

        viewModelScope.launch(ioDispatcher) {
            authRepository.startPhoneNumberVerification(phoneNumber, activity)
                .collect { state ->
                    when (state) {
                        is PhoneVerificationState.CodeSent -> {
                            _uiState.value = PhoneVerificationUiState.CodeSent(state.verificationId)
                            startCountdown()
                        }

                        PhoneVerificationState.Verified -> {
                            countdownJob?.cancel()
                            _uiState.value = PhoneVerificationUiState.Success
                        }

                        is PhoneVerificationState.Error -> {
                            _uiState.value = PhoneVerificationUiState.Error(
                                state.message ?: state.error.message ?: "Unknown error"
                            )
                        }

                        PhoneVerificationState.Idle -> Unit
                    }
                }
        }
    }


    fun verifyCode(verificationId: String, code: String) {
        signInWithCode(verificationId, code)
    }

    fun resendCode(activity: Activity) {
        _uiState.value = PhoneVerificationUiState.Verifying

        viewModelScope.launch(ioDispatcher) {
            when (
                val result =
                    authRepository.resendVerificationCode(
                        phoneNumber,
                        activity
                    )
            ) {
                is AppResult.Success -> {
                    startCountdown()
                    _uiState.value = PhoneVerificationUiState.CodeSent(result.data)
                }

                is AppResult.Error -> {
                    _uiState.value = PhoneVerificationUiState.Error(
                        result.exception.message ?: "Failed to resend code"
                    )
                }
            }
        }
    }

    private fun signInWithCode(
        verificationId: String,
        code: String
    ) {
        _uiState.value = PhoneVerificationUiState.Verifying

        viewModelScope.launch(ioDispatcher) {
            when (
                val result = authRepository.verifyPhoneCode(verificationId, code)
            ) {
                is AppResult.Success -> {
                    countdownJob?.cancel()
                    _uiState.value = PhoneVerificationUiState.Success
                }

                is AppResult.Error -> {
                    _uiState.value = PhoneVerificationUiState.Error(
                        result.exception.message ?: "Verification failed"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
    }
}
