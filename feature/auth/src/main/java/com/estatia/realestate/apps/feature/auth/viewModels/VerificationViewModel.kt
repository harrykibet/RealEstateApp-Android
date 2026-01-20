package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.feature.auth.state.VerificationUiState
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.firebase.auth.PhoneAuthProvider
import android.app.Activity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<VerificationUiState>(
            VerificationUiState.Countdown(120)
        )
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (second in 120 downTo 1) {
                _uiState.value = VerificationUiState.Countdown(second)
                delay(1_000)
            }
            _uiState.value = VerificationUiState.Expired
        }
    }

    fun verifyCode(verificationId: String, code: String) {
        _uiState.value = VerificationUiState.Verifying

        viewModelScope.launch(ioDispatcher) {
            val credential =
                PhoneAuthProvider.getCredential(verificationId, code)

            when (
                val result =
                    authRepository.signInWithPhoneAuthCredential(credential)
            ) {
                is Result.Success -> {
                    countdownJob?.cancel()
                    _uiState.value = VerificationUiState.Success
                }

                is Result.Error -> {
                    _uiState.value = VerificationUiState.Error(
                        result.exception.message ?: "Verification failed"
                    )
                }
            }
        }
    }

    fun resendCode(
        phoneNumber: String,
        activity: Activity,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ) {
        _uiState.value = VerificationUiState.Verifying

        viewModelScope.launch(ioDispatcher) {
            when (
                val result =
                    authRepository.resendVerificationCode(
                        phoneNumber,
                        activity,
                        resendingToken
                    )
            ) {
                is Result.Success -> {
                    startCountdown()
                    _uiState.value = VerificationUiState.Countdown(120)
                }

                is Result.Error -> {
                    _uiState.value = VerificationUiState.Error(
                        result.exception.message ?: "Failed to resend code"
                    )
                }
            }
        }
    }

    fun restartCountdown() {
        startCountdown()
    }

    override fun onCleared() {
        countdownJob?.cancel()
    }
}

