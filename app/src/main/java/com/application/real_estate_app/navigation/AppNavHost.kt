package com.application.real_estate_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.real_estate_app.feature_auth.navigation.authGraph
import com.application.real_estate_app.feature_home.navigation.homeGraph
import com.application.real_estate_app.feature_profile.navigation.profileGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.application.real_estate_app.feature_auth.navigation.LoginRoute
import com.application.real_estate_app.feature_home.navigation.HomeRoute
import com.application.real_estate_app.feature_home.navigation.navigateToHome
import com.application.real_estate_app.ui.RealEstateAppState

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
        HomeRoute // or HomeRoute if you're using constants
    } else {
        LoginRoute // or LoginRoute if you're using constants
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
                navController.navigateToHome(
                    navOptions = navOptions { popUpTo(LoginRoute) { inclusive = true } }
                )
            }
        )

        // ----------- HOME GRAPH -----------
        homeGraph(
            onNavigateToPropertyDetail = { propertyId ->
                navController.navigateToPropertyDetail(propertyId)
            }
        )

        // ----------- PROFILE GRAPH -----------
        profileGraph(
            onBack = { navController.popBackStack() }
        )

        // ----------- SEARCH  GRAPH -----------
        searchGraph(
            onBack = { navController.popBackStack() }
        )

        // ----------- PROPERTY ADDITION GRAPH -----------
        propertyAdditionGraph(
            onBack = { navController.popBackStack() }
        )
    }
}

