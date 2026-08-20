package com.estatia.realestate.apps.feature.favorites.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.core.navigation.routes.FavoritesBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.FavoritesRoute
import com.estatia.realestate.apps.feature.favorites.ui.screens.FavoritesRoute as FavoritesScreen

fun NavController.navigateToFavorites(navOptions: NavOptions? = null) =
    navigate(FavoritesRoute, navOptions)

fun NavGraphBuilder.favoritesGraph(
    onPropertyClick: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit
) {
    navigation<FavoritesBaseRoute>(startDestination = FavoritesRoute) {
        composable<FavoritesRoute> {
            FavoritesScreen(
                onPropertyClick = onPropertyClick,
                commentsContent = commentsContent
            )
        }
    }
}
