package com.estatia.realestate.apps.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.estatia.realestate.apps.feature.home.ui.screens.HomeRoute as HomeRouteScreen
import com.estatia.realestate.apps.feature.property.ui.screens.PropertyDetailsRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions)

fun NavController.navigateToPropertyDetail(propertyId: String) =
    navigate(route = PropertyDetailRoute(propertyId))

fun NavGraphBuilder.homeGraph(
    onBackClick: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute>(
            deepLinks = listOf(
                navDeepLink {
                    /**
                     * This destination has a deep link that enables a specific property to be
                     * opened from a notification (@see SystemTrayNotifier for more). The property 
                     * ID is sent in the URI rather than being modeled in the route type because it's
                     * transient data (stored in SavedStateHandle) that is cleared after the user has
                     * opened the property detail screen.
                     */
                    uriPattern = "DEEP_LINK_URI_PATTERN"
                },
            ),
        ) {
            HomeRouteScreen(
                onNavigateToPropertyDetail = onNavigateToPropertyDetail,
            )
        }

        composable<PropertyDetailRoute> { backStackEntry ->
            val route: PropertyDetailRoute = backStackEntry.toRoute()
            PropertyDetailsRoute(
                propertyId = route.propertyId,
                onBackClick = onBackClick
            )
        }
    }
}
