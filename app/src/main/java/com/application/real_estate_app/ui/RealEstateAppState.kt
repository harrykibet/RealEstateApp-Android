package com.application.real_estate_app.ui

import androidx.navigation.NavHostController

/**
 * Holds central navigation and app-level shared state.
 * Use [rememberMainAppState] to instantiate.
 */
class MainAppState(
    val navController: NavHostController
) {
    /**
     * Top-level destinations enum (for BottomBar or Tabs if needed)
     * e.g. HOME, PROFILE, SETTINGS, etc.
     */
    val topLevelDestinations = listOf(
        RealEstateAppDestination.HOME,
        RealEstateAppDestination.PROFILE,
        RealEstateAppDestination.EXPLORE
    )

    /**
     * Navigate to a top-level destination and pop everything else.
     */
    fun navigateToTopLevelDestination(destination: RealEstateAppDestination) {
        navController.navigate(destination.route) {
            popUpTo(0) // pops all
            launchSingleTop = true
        }
    }
}

enum class RealEstateAppDestination(val route: String) {
    HOME("home"),
    PROFILE("profile"),
    EXPLORE("explore"),
    AUTH("auth")
}
