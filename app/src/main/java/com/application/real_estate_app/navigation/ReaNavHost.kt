package com.application.real_estate_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.real_estate_app.feature_auth.navigation.authGraph
import com.application.real_estate_app.feature_home.navigation.homeGraph
import com.application.real_estate_app.feature_profile.navigation.profileGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.application.real_estate_app.feature_auth.navigation.AuthBaseRoute
import com.application.real_estate_app.feature_auth.navigation.LoginRoute
import com.application.real_estate_app.feature_home.navigation.HomeBaseRoute
import com.application.real_estate_app.feature_home.navigation.navigateToHome
import com.application.real_estate_app.feature_home.navigation.navigateToPropertyDetail
import com.application.real_estate_app.feature_property.navigation.propertyAdditionGraph
import com.application.real_estate_app.feature_search.navigation.searchGraph
import com.application.real_estate_app.ui.ReaAppState

@Composable
fun ReaNavHost(
    appState: ReaAppState,
    isUserAuthenticated: Boolean,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    // Determine the dynamic start destination
    val startDestination: Any = if (isUserAuthenticated) HomeBaseRoute else AuthBaseRoute

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {

        authGraph(
            onAuthenticated = {
                // Navigate to the home graph after successful login
                navController.navigateToHome(
                    navOptions = navOptions { popUpTo(LoginRoute) { inclusive = true } }
                )
            }
        )

        homeGraph(
            onNavigateToPropertyDetail = { propertyId ->
                navController.navigateToPropertyDetail(propertyId)
            },
            onBackClick = navController::popBackStack
        )

        profileGraph(onBackClick = navController::popBackStack)

        searchGraph(onBackClick = navController::popBackStack)

        propertyAdditionGraph(onBackClick = navController::popBackStack)
    }
}

