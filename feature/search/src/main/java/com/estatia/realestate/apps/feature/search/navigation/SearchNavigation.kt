package com.estatia.realestate.apps.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.feature.search.ui.screens.MapWithSearchBar
import kotlinx.serialization.Serializable

@Serializable data object SearchRoute // route to Search screen

@Serializable data object SearchBaseRoute // route to base navigation graph

fun NavController.navigateToSearch(navOptions: NavOptions? = null) = navigate(SearchRoute, navOptions)

fun NavGraphBuilder.searchGraph(
    onBackClick: () -> Unit
) {
    navigation<SearchBaseRoute>(startDestination = SearchRoute) {
        composable<SearchRoute> {
            MapWithSearchBar()
        }
    }
}