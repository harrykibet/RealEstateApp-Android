package com.estatia.realestate.apps.feature.profile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.feature.profile.ui.state.ProfileStats
import com.estatia.realestate.apps.feature.profile.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the user profile screen.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the display and editing of user identity and statistics.
 * - Concurrency: Thread-safe via [viewModelScope].
 * - Resilience: Surfaces authenticated state errors via [ProfileUiState.error].
 * - Observability: Tracks profile load success and failure rates.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository,
    private val metricsTracker: IMetricsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, error = "User not authenticated") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = userRepository.getUserById(userId)) {
                is AppResult.Success -> {
                    metricsTracker.incrementCounter("profile.load.success")
                    val user = result.data
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            name = user.name ?: "Unknown User",
                            email = user.email ?: "",
                            bio = user.bio ?: "",
                            profilePictureUrl = user.profilePictureUrl,
                            userType = user.userType.displayName,
                            stats = ProfileStats(
                                propertyCount = user.propertyCount,
                                followerCount = user.followerCount,
                                followingCount = user.followingCount
                            ),
                            error = null
                        )
                    }
                }
                is AppResult.Error -> {
                    metricsTracker.incrementCounter("profile.load.failure")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.exception.message ?: "Failed to load profile"
                        ) 
                    }
                }
            }
        }
    }
}
