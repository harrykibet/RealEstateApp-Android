package com.application.real_estate_app.core_data.repositories

import com.application.real_estate_app.core_data.interfaces.IUserRepository
import com.application.real_estate_app.core_datastore.ReaPreferencesDataSource
import com.application.real_estate_app.core_model.user.User
import com.application.real_estate_app.core_model.user.UserData
import com.application.real_estate_app.core_model.utils.DarkThemeConfig
import com.application.real_estate_app.core_model.utils.ThemeBrand
import com.application.real_estate_app.core_network.interfaces.IUserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: IUserRemoteDataSource,
    private val reaPreferencesDataSource: ReaPreferencesDataSource,
    private val analyticsRepository: AnalyticsRepository
) : IUserRepository {
    override suspend fun getUserById(userId: String): User? {
        return remoteDataSource.getUserById(userId)
    }

    override val userData: Flow<UserData> = reaPreferencesDataSource.userData

    override suspend fun setFollowedPropertyIds(followedPropertyIds: Set<String>) {
        reaPreferencesDataSource.setFollowedPropertyIds(followedPropertyIds)
    }

    override suspend fun setPropertyIdFollowed(followedPropertyId: String, followed: Boolean) {
        reaPreferencesDataSource.setPropertyIdFollowed(followedPropertyId, followed)
    }

    override suspend fun setPropertyBookmarked(propertyId: String, bookmarked: Boolean) {
        reaPreferencesDataSource.setPropertyBookmarked(propertyId, bookmarked)
    }

    override suspend fun setPropertyViewed(propertyId: String, viewed: Boolean) {
        reaPreferencesDataSource.setPropertyViewed(propertyId, viewed)
    }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        reaPreferencesDataSource.setThemeBrand(themeBrand)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        reaPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        reaPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        reaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }
}