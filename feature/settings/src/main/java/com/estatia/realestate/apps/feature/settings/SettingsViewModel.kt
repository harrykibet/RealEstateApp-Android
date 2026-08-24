package com.estatia.realestate.apps.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.feature.settings.SettingsUiState.Loading
import com.estatia.realestate.apps.feature.settings.SettingsUiState.Success
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel for managing the user settings experience.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the display and mutation of local user preferences.
 * - Concurrency: Thread-safe reactive state mapping via [userDataRepository].
 * - Lifecycle: Automatically stops reactive flows after 5 seconds of inactivity.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: IUserRepository,
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { _ ->
                Success(
                    settings = UserEditableSettings(),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = Loading,
            )
}

/**
 * Represents the settings which the user can edit within the app.
 */
class UserEditableSettings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}
