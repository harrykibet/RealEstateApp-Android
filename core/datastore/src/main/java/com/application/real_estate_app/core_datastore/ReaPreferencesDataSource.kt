package com.application.real_estate_app.core_datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.application.real_estate_app.core_model.user.UserData
import com.application.real_estate_app.core_model.utils.ThemeBrand
import com.application.real_estate_app.core_model.utils.DarkThemeConfig
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ReaPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                bookmarkedProperties = it.bookmarkedPropertyIdsMap.keys,
                viewedProperties = it.viewedPropertyIdsMap.keys,
                followedProperties = it.followedPropertyIdsMap.keys,
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
            Log.e("ReaPreferences", "Failed to update user preferences", ioException)
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
            Log.e("ReaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
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
            Log.e("ReaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setPropertyViewed(propertyId: String, viewed: Boolean) {
        setPropertiesViewed(listOf(propertyId), viewed)
    }

    suspend fun setPropertiesViewed(propertyIds: List<String>, viewed: Boolean) {
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
            Log.e("ReaPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedPropertyIds.isEmpty() && followedOwnerIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}
