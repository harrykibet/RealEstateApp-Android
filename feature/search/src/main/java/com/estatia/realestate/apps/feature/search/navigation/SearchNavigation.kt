package com.estatia.realestate.apps.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.feature.search.ui.screens.MapWithSearchBar

fun NavController.navigateToSearch(navOptions: NavOptions? = null) =
    navigate(SearchRoute, navOptions)

fun NavGraphBuilder.searchGraph(
    onBackClick: () -> Unit,
) {
    navigation<SearchBaseRoute>(startDestination = SearchRoute) {
        composable<SearchRoute> {
            MapWithSearchBar()
        }
    }
}
