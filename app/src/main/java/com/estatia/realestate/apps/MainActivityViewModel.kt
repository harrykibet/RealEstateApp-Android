package com.estatia.realestate.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.data.interfaces.IUserRepository
import com.estatia.realestate.apps.core.model.user.UserData
import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.model.utils.ThemeBrand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: IUserRepository
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    sealed interface MainActivityUiState {
        data object Loading : MainActivityUiState

        data class Success(val userData: UserData) : MainActivityUiState {
            override val shouldDisableDynamicTheming = !userData.useDynamicColor

            override val shouldUseAndroidTheme: Boolean = when (userData.themeBrand) {
                ThemeBrand.DEFAULT -> false
                ThemeBrand.ANDROID -> true
            }

            override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
                when (userData.darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                    DarkThemeConfig.LIGHT -> false
                    DarkThemeConfig.DARK -> true
                }
        }

        /**
         * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
         */
        fun shouldKeepSplashScreen() = this is Loading

        /**
         * Returns `true` if the dynamic color is disabled.
         */
        val shouldDisableDynamicTheming: Boolean get() = true

        /**
         * Returns `true` if the Android theme should be used.
         */
        val shouldUseAndroidTheme: Boolean get() = false

        /**
         * Returns `true` if dark theme should be used.
         */
        fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
    }
}
