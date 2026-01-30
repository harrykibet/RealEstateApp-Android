package com.estatia.realestate.apps.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.estatia.realestate.apps.feature.auth.navigation.authGraph
import com.estatia.realestate.apps.feature.home.navigation.homeGraph
import com.estatia.realestate.apps.feature.profile.navigation.profileGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.estatia.realestate.apps.feature.auth.navigation.AuthRoutes
import com.estatia.realestate.apps.feature.comments.navigation.commentsGraph
import com.estatia.realestate.apps.feature.home.navigation.HomeBaseRoute
import com.estatia.realestate.apps.feature.home.navigation.navigateToHome
import com.estatia.realestate.apps.feature.home.navigation.navigateToPropertyDetail
import com.estatia.realestate.apps.feature.property.navigation.propertyAdditionGraph
import com.estatia.realestate.apps.feature.search.navigation.searchGraph
import com.estatia.realestate.apps.ui.EstatiaAppState

@Composable
fun EstatiaNavHost(
    appState: EstatiaAppState,
    isUserAuthenticated: Boolean,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    // Determine the dynamic start destination
    val startDestination: Any = if (isUserAuthenticated) HomeBaseRoute else AuthRoutes.LOGIN


    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier,
    ) {

        authGraph(
            onAuthenticated = {
                // Navigate to the home graph after successful login
                navController.navigateToHome(
                    navOptions = navOptions { popUpTo(AuthRoutes.LOGIN) { inclusive = true } }
                )
            },
            navController = navController
        )

        homeGraph(
            onNavigateToPropertyDetail = { propertyId ->
                navController.navigateToPropertyDetail(propertyId)
            },
            onBackClick = navController::popBackStack
        )

        profileGraph(onBackClick = navController::popBackStack)

        searchGraph(onBackClick = navController::popBackStack)

        commentsGraph(onBackClick = navController::popBackStack)

        propertyAdditionGraph(onBackClick = navController::popBackStack)
    }
}

