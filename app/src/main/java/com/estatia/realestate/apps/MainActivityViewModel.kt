package com.estatia.realestate.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.domain.interfaces.IUserRepository
import com.estatia.realestate.apps.core.model.user.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: IUserRepository,
    authRepository: IAuthRepository,
    config: IConfigProvider
) : ViewModel() {
    private val _isInPiPMode = MutableStateFlow(false)
    val isInPiPMode = _isInPiPMode.asStateFlow()

    val uiState: StateFlow<MainActivityUiState> = combine(
        userDataRepository.userData,
        authRepository.isUserAuthenticated(),
        config.isReady
    ) { userData, isAuthenticated, isConfigReady ->
        if (isConfigReady) {
            MainActivityUiState.Success(userData, isAuthenticated)
        } else {
            MainActivityUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    sealed interface MainActivityUiState {
        data object Loading : MainActivityUiState

        data class Success(
            val userData: UserData,
            val isAuthenticated: Boolean
        ) : MainActivityUiState

        /**
         * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
         */
        fun shouldKeepSplashScreen() = this is Loading
    }

    fun updatePiPMode(inPiP: Boolean) {
        _isInPiPMode.value = inPiP
    }
}
