package com.estatia.realestate.apps.feature.favorites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.estatia.realestate.apps.feature.favorites.ui.screens.FavoritesScreen

fun NavController.navigateToFavorites(navOptions: NavOptions? = null) =
    navigate(FavoritesRoute, navOptions)

fun NavGraphBuilder.favoritesRoute() {
    composable<FavoritesRoute> {
        FavoritesScreen(
            favoriteProperties = emptyList(),
            onLikeClick = {},
            onCommentClick = {},
            onShareClick = {},
            onPropertyClick = {},
        )
    }
}
