package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.feature.auth.state.AuthState
import com.estatia.realestate.apps.feature.auth.state.AuthState.Authenticated
import com.estatia.realestate.apps.feature.auth.state.AuthState.EmailVerificationRequired
import com.estatia.realestate.apps.feature.auth.state.AuthState.PhoneVerificationRequired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private fun determineAuthState(user: AuthUserDomainModel?): AuthState {
        if (user == null) return AuthState.Unauthenticated

        if (!user.isEmailVerified) {
            return EmailVerificationRequired(
                email = user.email.orEmpty()
            )
        }

        if (user.phoneNumber.isNullOrEmpty()) {
            return PhoneVerificationRequired(
                phoneNumber = user.phoneNumber
            )
        }

        return Authenticated(
            user = user
        )
    }

    fun loginWithEmail(
        email: String,
        password: String
    ) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signInWithEmail(email, password)) {
                is Result.Success ->
                    _authState.value = determineAuthState(result.data)

                is Result.Failure ->
                    _authState.value = AuthState.Error(
                        result.exception.message ?: "Login failed"
                    )
            }
        }
    }

    /**
     * 🔑 Called AFTER Google ID Token is obtained via Credential Manager
     */
    fun loginWithGoogleIdToken(idToken: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signInWithGoogle(idToken)) {
                is Result.Success ->
                    _authState.value = determineAuthState(result.data)

                is Result.Failure ->
                    _authState.value = AuthState.Error(
                        result.exception.message ?: "Google sign-in failed"
                    )
            }
        }
    }

    fun checkExistingSession() {
        _authState.value =
            determineAuthState(authRepository.getCurrentUser())
    }
}
