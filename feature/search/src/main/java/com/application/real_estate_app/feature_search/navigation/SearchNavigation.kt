package com.application.real_estate_app.feature_search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable data object ExploreRoute // route to Explore screen

@Serializable data object ExploreBaseRoute // route to base navigation graph

fun NavGraphBuilder.searchGraph(
    onBackClick: () -> Unit
) {
    navigation<ExploreBaseRoute>(startDestination = ExploreRoute) {
        composable<ExploreRoute> {
            ExploreRoute()
        }
    }
}