package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserData
import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.model.utils.ThemeBrand
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level repository for user profile and preference management.
 */
interface IUserRepository {
    /**
     * Fetches detailed profile for a specific user.
     */
    suspend fun getUserById(userId: String): AppResult<UserDomainModel>

    /**
     * Reactive stream of current user's local preferences (Theme, dynamic color, etc.)
     */
    val userData: Flow<UserData>

    /**
     * Updates the set of followed properties.
     */
    suspend fun setFollowedPropertyIds(followedPropertyIds: Set<String>)

    /**
     * Toggles follow status for a property.
     */
    suspend fun setPropertyIdFollowed(followedPropertyId: String, followed: Boolean)

    /**
     * Toggles bookmarked status for a property.
     */
    suspend fun setPropertyBookmarked(propertyId: String, bookmarked: Boolean)

    /**
     * Toggles liked status for a property locally.
     */
    suspend fun setPropertyIdLiked(propertyId: String, liked: Boolean)

    /**
     * Sets a property as viewed.
     */
    suspend fun setPropertyViewed(propertyId: String, viewed: Boolean)

    /**
     * Sets the preferred theme brand.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the preferred dark theme configuration.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Toggles dynamic color support.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets onboarding completion status.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    /**
     * Sets the global mute preference.
     */
    suspend fun setIsMuted(isMuted: Boolean)
}
