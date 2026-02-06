package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.feature.auth.state.EmailVerificationUiState
import com.estatia.realestate.apps.core.common.errors.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    @param:Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<EmailVerificationUiState>(EmailVerificationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun sendVerificationEmail() {
        _uiState.value = EmailVerificationUiState.Sending

        viewModelScope.launch(ioDispatcher) {
            when (authRepository.sendEmailVerification()) {
                is Result.Success ->
                    _uiState.value = EmailVerificationUiState.EmailSent

                is Result.Error ->
                    _uiState.value = EmailVerificationUiState.Error("Failed to send email")
            }
        }
    }

    fun checkVerificationStatus() {
        _uiState.value = EmailVerificationUiState.Checking

        viewModelScope.launch(ioDispatcher) {
            when (val result = authRepository.isEmailVerified()) {
                is Result.Success -> {
                    _uiState.value =
                        if (result.data)
                            EmailVerificationUiState.Verified
                        else
                            EmailVerificationUiState.EmailSent
                }

                is Result.Error ->
                    _uiState.value =
                        EmailVerificationUiState.Error("Verification check failed")
            }
        }
    }
}
