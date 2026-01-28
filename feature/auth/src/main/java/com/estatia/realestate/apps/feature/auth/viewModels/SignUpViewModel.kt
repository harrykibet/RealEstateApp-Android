package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.data.repositories.AuthRepository
import com.estatia.realestate.apps.feature.auth.state.SignUpAction
import com.estatia.realestate.apps.feature.auth.state.SignUpFormState
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.user.User
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.feature.auth.events.SignUpEvent
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @param:Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpFormState())
    val state: StateFlow<SignUpFormState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SignUpEvent>()
    val events = _events.asSharedFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.UserNameChanged ->
                update { copy(userName = action.value) }

            is SignUpAction.EmailChanged ->
                update { copy(email = action.value) }

            is SignUpAction.PhoneChanged ->
                update { copy(phone = action.value) }

            is SignUpAction.PasswordChanged ->
                update { copy(password = action.value) }

            is SignUpAction.UserTypeChanged ->
                update { copy(userType = action.value) }

            SignUpAction.Submit ->
                signUp()
        }
    }

    private fun signUp() {
        val current = state.value

        if (
            current.email.isBlank() ||
            current.password.isBlank() ||
            current.userType.isBlank()
        ) {
            update { copy(error = "Please fill all required fields") }
            return
        }

        viewModelScope.launch(ioDispatcher) {
            update { copy(isLoading = true, error = null) }

            when (
                val authResult = authRepository.signUpWithEmail(
                    current.email,
                    current.password
                )
            ) {
                is Result.Success -> {
                    handleUserRegistration(
                        authResult = authResult.data,
                        current = current
                    )
                }

                is Result.Error -> {
                    update {
                        copy(
                            isLoading = false,
                            error = authResult.exception.message
                        )
                    }
                }
            }
        }
    }

    private suspend fun handleUserRegistration(
        authResult: AuthResult,
        current: SignUpFormState
    ) {
        val user = User(
            userId = authResult.user!!.uid,
            name = current.userName,
            email = current.email,
            phoneNumber = current.phone,
            profilePictureUrl = null,
            userType = UserType.valueOf(current.userType),
            verified = false,
            likedProperties = emptyList()
        )

        when (
            val result = authRepository.createUserIfNotExists(
                user.userId!!,
                user
            )
        ) {
            is Result.Success -> {
                emitNextAuthStep(current)
                update { copy(isLoading = false) }
            }

            is Result.Error -> {
                update {
                    copy(
                        isLoading = false,
                        error = result.exception.message
                            ?: "Failed to create user profile"
                    )
                }
            }
        }
    }

    private inline fun update(
        block: SignUpFormState.() -> SignUpFormState
    ) {
        _state.value = _state.value.block()
    }

    private suspend fun emitNextAuthStep(current: SignUpFormState) {
        if (current.phone.isNotBlank()) {
            _events.emit(SignUpEvent.RequirePhoneVerification(
                phone = current.phone
            ))
        } else {
            _events.emit(SignUpEvent.RequireEmailVerification(
                email = current.email
            ))
        }
    }
}

