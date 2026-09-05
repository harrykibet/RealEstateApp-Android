package com.estatia.realestate.apps.feature.auth.viewModels

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.feature.auth.actions.SignUpAction
import com.estatia.realestate.apps.feature.auth.state.SignUpFormState
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.model.user.VerificationLevel
import com.estatia.realestate.apps.feature.auth.events.SignUpEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_USER_NAME = "signup_user_name"
private const val KEY_EMAIL = "signup_email"
private const val KEY_PHONE = "signup_phone"
private const val KEY_USER_TYPE = "signup_user_type"

@ViewModelMarker
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val metricsTracker: IMetricsTracker,
    private val savedStateHandle: SavedStateHandle,
    @param:Dispatcher(EstatiaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(
        SignUpFormState(
            userName = savedStateHandle.get<String>(KEY_USER_NAME).orEmpty(),
            email = savedStateHandle.get<String>(KEY_EMAIL).orEmpty(),
            phone = savedStateHandle.get<String>(KEY_PHONE).orEmpty(),
            userType = savedStateHandle.get<String>(KEY_USER_TYPE).orEmpty()
        )
    )
    val state: StateFlow<SignUpFormState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SignUpEvent>()
    val events = _events.asSharedFlow()

    init {
        // 🛡️ State Restoration: Persist non-sensitive form changes
        _state.onEach { form ->
            savedStateHandle[KEY_USER_NAME] = form.userName
            savedStateHandle[KEY_EMAIL] = form.email
            savedStateHandle[KEY_PHONE] = form.phone
            savedStateHandle[KEY_USER_TYPE] = form.userType
        }.launchIn(viewModelScope)
    }

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

        // 🛡️ Idempotency: Prevent duplicate submissions
        if (current.isLoading) return

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
                is AppResult.Success -> {
                    handleUserRegistration(
                        authResult = authResult.data,
                        current = current
                    )
                }

                is AppResult.Error -> {
                    metricsTracker.incrementCounter("auth.signup.failure")
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
        authResult: AuthUserDomainModel,
        current: SignUpFormState
    ) {
        val userId = authResult.userId

        val userDomainModel = UserDomainModel(
            userId = userId,
            name = current.userName,
            email = current.email,
            phoneNumber = current.phone,
            profilePictureUrl = null,
            userType = UserType.valueOf(current.userType),
            verificationLevel = VerificationLevel.NONE,
            likedProperties = emptyList()
        )

        when (
            val result = authRepository.createOrUpdateUserProfile(
                userId,
                userDomainModel
            )
        ) {
            is AppResult.Success -> {
                metricsTracker.incrementCounter("auth.signup.profile_success")
                emitNextAuthStep(current)
                update { copy(isLoading = false) }
            }

            is AppResult.Error -> {
                metricsTracker.incrementCounter("auth.signup.profile_failure")
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
