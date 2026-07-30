package com.estatia.realestate.apps.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.estatia.realestate.apps.feature.home.ui.screens.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute // route to Home screen

@Serializable
data object HomeBaseRoute // route to base navigation graph

@Serializable
data class PropertyDetailRoute( val propertyId: String) // route to property detail screen

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(route = HomeRoute, navOptions)

fun NavController.navigateToPropertyDetail(propertyId: String) = navigate(route = PropertyDetailRoute(propertyId))

fun NavGraphBuilder.homeGraph(
    onBackClick: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute>(
            deepLinks = listOf(
                navDeepLink {
                    /**
                     * This destination has a deep link that enables a specific news resource to be
                     * opened from a notification (@see SystemTrayNotifier for more). The news resource
                     * ID is sent in the URI rather than being modeled in the route type because it's
                     * transient data (stored in SavedStateHandle) that is cleared after the user has
                     * opened the news resource.
                     */
                    /**
                     * This destination has a deep link that enables a specific news resource to be
                     * opened from a notification (@see SystemTrayNotifier for more). The news resource
                     * ID is sent in the URI rather than being modeled in the route type because it's
                     * transient data (stored in SavedStateHandle) that is cleared after the user has
                     * opened the news resource.
                     */
                    uriPattern = "DEEP_LINK_URI_PATTERN"
                },
            ),
        ) {
            HomeRoute()
        }
    }
}