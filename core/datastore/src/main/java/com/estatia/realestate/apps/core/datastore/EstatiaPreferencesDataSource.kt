package com.estatia.realestate.apps.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.estatia.realestate.apps.core.model.user.UserData
import com.estatia.realestate.apps.core.model.utils.ThemeBrand
import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * Data source for application preferences using Jetpack DataStore.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: Source of truth for local user settings and UI configuration.
 * - Concurrency: Thread-safe; DataStore handles atomic disk writes and multi-process safety.
 * - Resilience: Surfaces [UserData] flow; handles [IOException] during updates.
 * - Observability: Tracks preference update failures.
 */
class EstatiaPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
    private val metricsTracker: IMetricsTracker,
    private val logger: ILogger
) {
    val userData = userPreferences.data
        .map {
            UserData(
                bookmarkedProperties = it.bookmarkedPropertyIdsMap.keys,
                viewedProperties = it.viewedPropertyIdsMap.keys,
                followedProperties = it.followedPropertyIdsMap.keys,
                likedProperties = it.likedPropertyIdsMap.keys,
                themeBrand = when (it.themeBrand) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT,
                        -> ThemeBrand.DEFAULT
                    ThemeBrandProto.THEME_BRAND_ANDROID -> ThemeBrand.ANDROID
                },
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        ->
                        DarkThemeConfig.FOLLOW_SYSTEM
                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
                isMuted = it.isMuted,
            )
        }

    suspend fun setFollowedPropertyIds(propertyIds: Set<String>) {
        try {
            userPreferences.updateData {
                it.copy {
                    followedPropertyIds.clear()
                    followedPropertyIds.putAll(propertyIds.associateWith { true })
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update followed properties", ioException)
        }
    }

    suspend fun setPropertyIdFollowed(propertyId: String, followed: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (followed) {
                        followedPropertyIds.put(propertyId, true)
                    } else {
                        followedPropertyIds.remove(propertyId)
                    }
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update followed state", ioException)
        }
    }

    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.themeBrand = when (themeBrand) {
                        ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                        ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                    }
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update theme brand", ioException)
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        try {
            userPreferences.updateData {
                it.copy { this.useDynamicColor = useDynamicColor }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update dynamic color preference", ioException)
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.darkThemeConfig = when (darkThemeConfig) {
                        DarkThemeConfig.FOLLOW_SYSTEM ->
                            DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                        DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                        DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                    }
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update dark theme config", ioException)
        }
    }

    suspend fun setPropertyBookmarked(propertyId: String, bookmarked: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (bookmarked) {
                        bookmarkedPropertyIds.put(propertyId, true)
                    } else {
                        bookmarkedPropertyIds.remove(propertyId)
                    }
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update bookmarked state", ioException)
        }
    }

    suspend fun setPropertyLiked(propertyId: String, liked: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (liked) {
                        likedPropertyIds.put(propertyId, true)
                    } else {
                        likedPropertyIds.remove(propertyId)
                    }
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update liked state", ioException)
        }
    }

    suspend fun setPropertyViewed(propertyId: String, viewed: Boolean) {
        setPropertiesViewed(listOf(propertyId), viewed)
    }

    suspend fun setPropertiesViewed(propertyIds: List<String>, viewed: Boolean) {
        try {
            userPreferences.updateData { prefs ->
                prefs.copy {
                    propertyIds.forEach { id ->
                        if (viewed) {
                            viewedPropertyIds.put(id, true)
                        } else {
                            viewedPropertyIds.remove(id)
                        }
                    }
                }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update viewed properties", ioException)
        }
    }

    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                propertyVersion = it.propertyChangeListVersion,
                userVersion = it.userChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * Update the [ChangeListVersions] using [update].
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        propertyVersion = currentPreferences.propertyChangeListVersion,
                        userVersion = currentPreferences.userChangeListVersion,
                    ),
                )

                currentPreferences.copy {
                    propertyChangeListVersion = updatedChangeListVersions.propertyVersion
                    userChangeListVersion = updatedChangeListVersions.userVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("EstatiaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        try {
            userPreferences.updateData {
                it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update onboarding state", ioException)
        }
    }

    suspend fun setIsMuted(isMuted: Boolean) {
        try {
            userPreferences.updateData {
                it.copy { this.isMuted = isMuted }
            }
        } catch (ioException: IOException) {
            metricsTracker.incrementCounter("datastore.update.failure")
            logger.e("EstatiaPreferences", "Failed to update mute state", ioException)
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedPropertyIds.isEmpty() && followedOwnerIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}
