package com.application.real_estate_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    appState: RealEstateAppState,
    isUserAuthenticated: Boolean,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    // Determine the dynamic start destination
    val startDestination = if (isUserAuthenticated) {
        "home" // or HomeBaseRoute if you're using constants
    } else {
        "auth"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // ----------- AUTH GRAPH -----------
        authGraph(
            onAuthenticated = {
                // Navigate to the home graph after successful login
                navController.navigate("home") {
                    popUpTo("auth") { inclusive = true }
                }
            }
        )

        // ----------- HOME GRAPH -----------
        homeGraph(
            onShowSnackbar = onShowSnackbar,
            onNavigateToProfile = { navController.navigate("profile") }
        )

        // ----------- PROFILE GRAPH -----------
        profileGraph(
            onBack = { navController.popBackStack() }
        )
    }
}

