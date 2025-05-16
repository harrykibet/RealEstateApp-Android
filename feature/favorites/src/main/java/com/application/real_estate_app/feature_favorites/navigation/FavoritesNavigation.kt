package com.application.real_estate_app.feature_favorites.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.application.real_estate_app.feature_favorites.ui.screens.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable data object FavoritesRoute // route to Favorites screen

@Serializable data object FavoritesBaseRoute // route to base navigation graph

fun NavGraphBuilder.favoritesRoute() {
    composable<FavoritesRoute> {
        FavoritesScreen()
    }
}