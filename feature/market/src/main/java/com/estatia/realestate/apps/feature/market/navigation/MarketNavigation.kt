package com.estatia.realestate.apps.feature.market.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.core.navigation.routes.MarketBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.MarketRoute
import com.estatia.realestate.apps.feature.market.ui.MarketRoute as MarketScreen

fun NavController.navigateToMarket(navOptions: NavOptions? = null) =
    navigate(route = MarketRoute, navOptions)

fun NavGraphBuilder.marketGraph(
    onItemClick: (String) -> Unit
) {
    navigation<MarketBaseRoute>(startDestination = MarketRoute) {
        composable<MarketRoute> {
            MarketScreen(
                onItemClick = onItemClick
            )
        }
    }
}
