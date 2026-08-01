package com.estatia.realestate.apps.feature.search.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.core.navigation.SearchBaseRoute
import com.estatia.realestate.apps.core.navigation.SearchRoute
import com.estatia.realestate.apps.feature.search.ui.screens.SearchRoute as SearchScreenRoute

fun NavController.navigateToSearch(navOptions: NavOptions? = null) =
    navigate(SearchRoute, navOptions)

fun NavGraphBuilder.searchGraph(
    onBackClick: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit
) {
    navigation<SearchBaseRoute>(startDestination = SearchRoute) {
        composable<SearchRoute> {
            SearchScreenRoute(
                onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                commentsContent = commentsContent
            )
        }
    }
}
