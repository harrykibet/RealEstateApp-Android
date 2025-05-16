package com.application.real_estate_app.ui

import androidx.navigation.NavHostController
import com.application.real_estate_app.navigation.TopLevelDestination

/**
 * Holds central navigation and app-level shared state.
 * Use [rememberMainAppState] to instantiate.
 */
class RealEstateAppState(
    val navController: NavHostController
) {
    /**
     * Top-level destinations enum (for BottomBar or Tabs if needed)
     * e.g. HOME, PROFILE, SETTINGS, etc.
     */
    val topLevelDestinations = listOf(
        TopLevelDestination.HOME,
        TopLevelDestination.PROFILE,
        TopLevelDestination.EXPLORE,
        TopLevelDestination.ADD_PROPERTY,
        TopLevelDestination.FAVORITES
    )

    /**
     * Navigate to a top-level destination and pop everything else.
     */
    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(0) // pops all
            launchSingleTop = true
        }
    }
}
