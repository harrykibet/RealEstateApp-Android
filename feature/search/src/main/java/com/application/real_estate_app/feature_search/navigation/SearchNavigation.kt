package com.application.real_estate_app.feature_search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.application.real_estate_app.feature_search.ui.screens.MapWithSearchBar
import kotlinx.serialization.Serializable

@Serializable data object ExploreRoute // route to Explore screen

@Serializable data object ExploreBaseRoute // route to base navigation graph

fun NavController.navigateToSearch(navOptions: NavOptions? = null) = navigate(ExploreRoute, navOptions)

fun NavGraphBuilder.searchGraph(
    onBackClick: () -> Unit
) {
    navigation<ExploreBaseRoute>(startDestination = ExploreRoute) {
        composable<ExploreRoute> {
            MapWithSearchBar()
        }
    }
}