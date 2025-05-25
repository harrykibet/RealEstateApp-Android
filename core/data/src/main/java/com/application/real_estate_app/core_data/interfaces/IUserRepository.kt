package com.application.real_estate_app.core_data.interfaces

import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_model.user.UserData
import com.application.real_estate_app.core_model.utils.DarkThemeConfig
import com.application.real_estate_app.core_model.utils.ThemeBrand
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun getUserById(userId: String): User?
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's currently followed properties
     */
    suspend fun setFollowedPropertyIds(followedPropertyIds: Set<String>)

    /**
     * Sets the user's newly followed/unfollowed properties
     */
    suspend fun setPropertyIdFollowed(followedPropertyId: String, followed: Boolean)

    /**
     * Updates the bookmarked status for a property
     */
    suspend fun setPropertyBookmarked(propertyId: String, bookmarked: Boolean)

    /**
     * Updates the viewed status for a property
     */
    suspend fun setPropertyViewed(propertyId: String, viewed: Boolean)

    /**
     * Sets the desired theme brand.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}