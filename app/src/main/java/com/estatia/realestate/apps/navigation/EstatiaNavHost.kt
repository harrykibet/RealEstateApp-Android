package com.estatia.realestate.apps.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.estatia.realestate.apps.feature.auth.navigation.AuthGraphRoute
import com.estatia.realestate.apps.feature.auth.navigation.LoginRoute
import com.estatia.realestate.apps.feature.auth.navigation.authGraph
import com.estatia.realestate.apps.feature.home.navigation.homeGraph
import com.estatia.realestate.apps.feature.profile.navigation.profileGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.estatia.realestate.apps.feature.comments.navigation.commentsGraph
import com.estatia.realestate.apps.feature.comments.navigation.navigateToComments
import com.estatia.realestate.apps.feature.favorites.navigation.favoritesGraph
import com.estatia.realestate.apps.feature.home.navigation.HomeBaseRoute
import com.estatia.realestate.apps.feature.home.navigation.navigateToHome
import com.estatia.realestate.apps.feature.home.navigation.navigateToPropertyDetail
import com.estatia.realestate.apps.feature.property.navigation.propertyAdditionGraph
import com.estatia.realestate.apps.feature.search.navigation.searchGraph
import com.estatia.realestate.apps.ui.EstatiaAppState

@Composable
fun EstatiaNavHost(
    appState: EstatiaAppState,
    isUserAuthenticated: Boolean?,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    // If auth state is still loading, show nothing (Splash screen is likely still visible)
    if (isUserAuthenticated == null) return

    // Determine the dynamic start destination
    val startDestination: Any = if (isUserAuthenticated) HomeBaseRoute else AuthGraphRoute

    // Global redirection logic
    LaunchedEffect(isUserAuthenticated) {
        if (!isUserAuthenticated) {
            navController.navigate(AuthGraphRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

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
            },
            navController = navController
        )

        homeGraph(
            onNavigateToPropertyDetail = { propertyId ->
                navController.navigateToPropertyDetail(propertyId)
            },
            onCommentClick = { propertyId ->
                navController.navigateToComments(propertyId)
            },
            onBackClick = navController::popBackStack,
        )

        profileGraph(
            onBackClick = navController::popBackStack,
            onLogoutClick = appState::signOut,
        )

        searchGraph(onBackClick = navController::popBackStack)

        commentsGraph(onBackClick = navController::popBackStack)

        favoritesGraph()

        propertyAdditionGraph(onBackClick = navController::popBackStack)
    }
}

