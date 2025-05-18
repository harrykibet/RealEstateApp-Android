package com.application.real_estate_app.feature_favorites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.application.real_estate_app.feature_favorites.ui.screens.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable data object FavoritesRoute // route to Favorites screen

@Serializable data object FavoritesBaseRoute // route to base navigation graph

fun NavController.navigateToFavorites(navOptions: NavOptions? = null) = navigate(FavoritesRoute, navOptions)

fun NavGraphBuilder.favoritesRoute() {
    composable<FavoritesRoute> {
        FavoritesScreen()
    }
}