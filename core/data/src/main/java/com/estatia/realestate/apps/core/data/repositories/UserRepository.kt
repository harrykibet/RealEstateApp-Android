package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.annotations.Repository
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.data.mappers.remote.RemoteUserProfileMapper
import com.estatia.realestate.apps.core.data.util.translateUserFailures
import com.estatia.realestate.apps.core.datastore.EstatiaPreferencesDataSource
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserData
import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.model.utils.ThemeBrand
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository for managing user profiles and local preferences.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: Source of truth for profile is [IUserRemoteDataSource]; local preferences are in [EstatiaPreferencesDataSource].
 * - Concurrency: Thread-safe via reactive streams (Flow) and atomic preference updates.
 * - Resilience: Translates remote exceptions to domain-specific user failures.
 * - Observability: Tracks profile retrieval latency and success/failure.
 */
@Repository
internal class UserRepository @Inject constructor(
    private val remoteDataSource: IUserRemoteDataSource,
    private val estatiaPreferencesDataSource: EstatiaPreferencesDataSource,
    private val metricsTracker: IMetricsTracker,
    private val exceptionTranslator: IExceptionTranslator
) : IUserRepository {

    override suspend fun getUserById(userId: String): AppResult<UserDomainModel> {
        val startTime = System.currentTimeMillis()
        return remoteDataSource.getUserById(userId)
            .map(RemoteUserProfileMapper::toDomain)
            .translateUserFailures(exceptionTranslator)
            .also { result ->
                val duration = System.currentTimeMillis() - startTime
                metricsTracker.trackDuration("user.profile.fetch_latency", duration.milliseconds)
                if (result is AppResult.Success) {
                    metricsTracker.incrementCounter("user.profile.fetch_success")
                } else {
                    metricsTracker.incrementCounter("user.profile.fetch_failure")
                }
            }
    }

    override val userData: Flow<UserData> = estatiaPreferencesDataSource.userData

    override suspend fun setFollowedPropertyIds(followedPropertyIds: Set<String>) {
        estatiaPreferencesDataSource.setFollowedPropertyIds(followedPropertyIds)
    }

    override suspend fun setPropertyIdFollowed(followedPropertyId: String, followed: Boolean) {
        estatiaPreferencesDataSource.setPropertyIdFollowed(followedPropertyId, followed)
    }

    override suspend fun setPropertyBookmarked(propertyId: String, bookmarked: Boolean) {
        estatiaPreferencesDataSource.setPropertyBookmarked(propertyId, bookmarked)
    }

    override suspend fun setPropertyIdLiked(propertyId: String, liked: Boolean) {
        estatiaPreferencesDataSource.setPropertyLiked(propertyId, liked)
    }

    override suspend fun setPropertyViewed(propertyId: String, viewed: Boolean) {
        estatiaPreferencesDataSource.setPropertyViewed(propertyId, viewed)
    }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        estatiaPreferencesDataSource.setThemeBrand(themeBrand)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        estatiaPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        estatiaPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        estatiaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }

    override suspend fun setIsMuted(isMuted: Boolean) {
        estatiaPreferencesDataSource.setIsMuted(isMuted)
    }
}
