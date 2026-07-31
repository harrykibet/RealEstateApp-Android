package com.estatia.realestate.apps.feature.favorites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.feature.favorites.ui.screens.FavoritesScreen

fun NavController.navigateToFavorites(navOptions: NavOptions? = null) =
    navigate(FavoritesRoute, navOptions)

fun NavGraphBuilder.favoritesGraph() {
    navigation<FavoritesBaseRoute>(startDestination = FavoritesRoute) {
        composable<FavoritesRoute> {
            FavoritesScreen(
                favoriteProperties = emptyList(),
                onLikeClick = {},
                onShareClick = {},
                onPropertyClick = {},
            )
        }
    }
}
