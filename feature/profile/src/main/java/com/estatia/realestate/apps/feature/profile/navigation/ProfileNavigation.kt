package com.estatia.realestate.apps.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.feature.profile.ui.screens.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable data object ProfileBaseRoute // route to base navigation graph

@Serializable data object ProfileRoute // route to Profile screen

fun NavController.navigateToProfile(navOptions: NavOptions? = null) = navigate(ProfileRoute, navOptions)

fun NavGraphBuilder.profileGraph(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    navigation<ProfileBaseRoute>(startDestination = ProfileRoute) {
        composable<ProfileRoute> {
            ProfileScreen(onLogoutClick = onLogoutClick)
        }
    }
}