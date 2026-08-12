package com.estatia.realestate.apps.core.model.user

import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.model.utils.ThemeBrand

/**
 * Class summarizing user interest data
 */
data class UserData(
    val bookmarkedProperties: Set<String>,
    val viewedProperties: Set<String>,
    val followedProperties: Set<String>,
    val likedProperties: Set<String>,
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val shouldHideOnboarding: Boolean,
    val isMuted: Boolean,
)








