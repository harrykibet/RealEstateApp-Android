package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.feature.auth.state.PhoneVerificationUiState
import com.estatia.realestate.apps.core.common.errors.Result
import com.google.firebase.auth.PhoneAuthProvider
import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
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

    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var activity: Activity? = null

    init {
        startCountdown()
    }

    fun attachActivity(activity: Activity) {
        this.activity = activity
    }

    fun setResendingToken(token: PhoneAuthProvider.ForceResendingToken) {
        resendingToken = token
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

    fun startPhoneNumberVerification() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                resendingToken = token
                _uiState.value = PhoneVerificationUiState.CodeSent(verificationId)
                startCountdown()
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.value = PhoneVerificationUiState.Error(e.message ?: "Verification failed")
            }
        }

        val options = PhoneAuthOptions.newBuilder(authRepository.getFirebaseAuth())
            .setPhoneNumber(phoneNumber)
            .setTimeout(120L, TimeUnit.SECONDS) // 2 Minutes
            .setActivity(activity!!)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    fun verifyCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    fun resendCode() {
        _uiState.value = PhoneVerificationUiState.Verifying

        val act = activity ?: return

        val token = resendingToken ?: return

        viewModelScope.launch(ioDispatcher) {
            when (
                val result =
                    authRepository.resendVerificationCode(
                        phoneNumber,
                        act,
                        token
                    )
            ) {
                is Result.Success -> {
                    startCountdown()
                    _uiState.value = PhoneVerificationUiState.Countdown(120)
                }

                is Result.Error -> {
                    _uiState.value = PhoneVerificationUiState.Error(
                        result.exception.message ?: "Failed to resend code"
                    )
                }
            }
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        _uiState.value = PhoneVerificationUiState.Verifying

        viewModelScope.launch(ioDispatcher) {
            when (
                val result = authRepository.signInWithPhoneAuthCredential(credential)
            ) {
                is Result.Success -> {
                    countdownJob?.cancel()
                    _uiState.value = PhoneVerificationUiState.Success
                }

                is Result.Error -> {
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

