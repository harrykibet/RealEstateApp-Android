package com.estatia.realestate.apps.feature.profile.ui.state
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiState

/**
 * Data class to model profile statistics.
 */
@Utility
data class ProfileStats(
    val propertyCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
)

/**
 * UI state for the Profile screen.
 */
@UiState
data class ProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String? = null,
    val userType: String = "",
    val stats: ProfileStats = ProfileStats(),
    val error: String? = null
)
