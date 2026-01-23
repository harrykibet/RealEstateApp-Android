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
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpFormState())
    val state: StateFlow<SignUpFormState> = _state.asStateFlow()

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

        // Basic validation (expand later)
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

            when(val result = authRepository.signUpWithEmail(
                current.email,
                current.password))
            {
                is Result.Success -> {
                    registerUser(
                        authResult = result.data,
                        email = current.email,
                        userName = current.userName,
                        phoneNumber = current.phone,
                        userType = UserType.valueOf(current.userType)
                    )
                    update { copy(isLoading = false) }
                }
                is Result.Error -> {
                    update { copy(isLoading = false, error = result.exception.message) }
                }
            }
        }
    }

    private suspend fun registerUser(
        authResult: AuthResult,
        email: String,
        userName: String,
        phoneNumber: String,
        userType: UserType
    ) {
        val user = User(
            userId = authResult.user!!.uid,
            name = userName,
            email = email,
            phoneNumber = phoneNumber,
            profilePictureUrl = null,
            userType = userType,
            verified = false,
            likedProperties = emptyList()
        )

        authRepository.createUserIfNotExists(user.userId, user)
    }

    private inline fun update(block: SignUpFormState.() -> SignUpFormState) {
        _state.value = _state.value.block()
    }
}
