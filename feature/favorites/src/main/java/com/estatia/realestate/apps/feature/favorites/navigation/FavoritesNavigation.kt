package com.estatia.realestate.apps.feature.favorites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.estatia.realestate.apps.feature.favorites.ui.screens.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable data object FavoritesRoute // route to Favorites screen

@Serializable data object FavoritesBaseRoute // route to base navigation graph

fun NavController.navigateToFavorites(navOptions: NavOptions? = null) = navigate(FavoritesRoute, navOptions)

fun NavGraphBuilder.favoritesRoute() {
    composable<FavoritesRoute> {
        FavoritesScreen(
            favoriteProperties = emptyList(),
            onLikeClick = {},
            onCommentClick = {},
            onShareClick = {},
            onPropertyClick = {}
        )
    }
}