package com.application.real_estate_app.feature_profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object ProfileRoute // route to Profile screen

fun NavGraphBuilder.profileRoute() {
    composable<ProfileRoute> {
        ProfileScreen()
    }
}