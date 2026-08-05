package com.estatia.realestate.apps.feature.profile.ui.state

/**
 * Data class to model profile statistics.
 */
data class ProfileStats(
    val propertyCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
)

/**
 * UI state for the Profile screen.
 */
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
