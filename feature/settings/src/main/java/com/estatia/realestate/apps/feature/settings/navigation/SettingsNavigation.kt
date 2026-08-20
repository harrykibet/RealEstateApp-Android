package com.estatia.realestate.apps.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.estatia.realestate.apps.core.navigation.routes.SettingsRoute
import com.estatia.realestate.apps.feature.settings.SettingsScreen

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(route = SettingsRoute, navOptions)

fun NavGraphBuilder.settingsGraph(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    composable<SettingsRoute> {
        SettingsScreen(
            onBackClick = onBackClick,
            onLogoutClick = onLogoutClick,
        )
    }
}
