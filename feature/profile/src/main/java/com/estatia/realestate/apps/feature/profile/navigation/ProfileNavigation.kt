package com.estatia.realestate.apps.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.core.navigation.ProfileBaseRoute
import com.estatia.realestate.apps.core.navigation.ProfileRoute
import com.estatia.realestate.apps.feature.profile.ui.screens.ProfileScreen

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(ProfileRoute, navOptions)

fun NavGraphBuilder.profileGraph(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    navigation<ProfileBaseRoute>(startDestination = ProfileRoute) {
        composable<ProfileRoute> {
            ProfileScreen(
                onSettingsClick = onSettingsClick,
            )
        }
    }
}
