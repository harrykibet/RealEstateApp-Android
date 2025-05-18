package com.application.real_estate_app.core_model.user

import com.application.real_estate_app.core_model.utils.DarkThemeConfig
import com.application.real_estate_app.core_model.utils.ThemeBrand

/**
 * Class summarizing user interest data
 */
data class UserData(
    val bookmarkedProperties: Set<String>,
    val viewedProperties: Set<String>,
    val followedProperties: Set<String>,
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val shouldHideOnboarding: Boolean,
)








