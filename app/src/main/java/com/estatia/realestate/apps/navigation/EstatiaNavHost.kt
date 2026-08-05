package com.estatia.realestate.apps.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.navigation.AuthBaseRoute
import com.estatia.realestate.apps.core.navigation.HomeBaseRoute
import com.estatia.realestate.apps.core.navigation.LoginRoute
import com.estatia.realestate.apps.feature.auth.navigation.authGraph
import com.estatia.realestate.apps.feature.home.navigation.homeGraph
import com.estatia.realestate.apps.feature.profile.navigation.profileGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.ui.screens.CommentSheetContent
import com.estatia.realestate.apps.feature.comments.ui.viewmodels.CommentsViewModel
import com.estatia.realestate.apps.feature.home.navigation.navigateToHome
import com.estatia.realestate.apps.feature.home.navigation.navigateToPropertyDetail
import com.estatia.realestate.apps.feature.property.navigation.propertyAdditionGraph
import com.estatia.realestate.apps.feature.property.navigation.propertyDetailsScreen
import com.estatia.realestate.apps.feature.search.navigation.searchGraph
import com.estatia.realestate.apps.feature.settings.navigation.settingsGraph
import com.estatia.realestate.apps.feature.settings.navigation.navigateToSettings
import com.estatia.realestate.apps.ui.EstatiaAppState

@Composable
fun EstatiaNavHost(
    appState: EstatiaAppState,
    isUserAuthenticated: Boolean?,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    // If auth state is still loading, show nothing (Splash screen is likely still visible)
    if (isUserAuthenticated == null) return

    // Determine the dynamic start destination
    val startDestination: Any = if (isUserAuthenticated) HomeBaseRoute else AuthBaseRoute

    // Global redirection logic
    LaunchedEffect(isUserAuthenticated) {
        if (!isUserAuthenticated) {
            navController.navigate(AuthBaseRoute) {
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
                    navOptions = navOptions { popUpTo(LoginRoute) { inclusive = true } },
                )
            },
            navController = navController
        )

        homeGraph(
            onNavigateToPropertyDetail = { propertyId ->
                navController.navigateToPropertyDetail(propertyId)
            },
            commentsContent = { propertyId ->
                val commentsViewModel: CommentsViewModel = hiltViewModel()
                LaunchedEffect(propertyId) {
                    commentsViewModel.onAction(CommentsAction.Load(propertyId))
                }
                val commentsState by commentsViewModel.state.collectAsState()
                CommentSheetContent(
                    state = commentsState,
                    onAction = commentsViewModel::onAction
                )
            }
        )

        profileGraph(
            onEditProfileClick = { /* TODO: Navigate to Edit Profile */ },
            onSettingsClick = { navController.navigateToSettings() },
        )

        searchGraph(
            onBackClick = navController::popBackStack,
            onNavigateToPropertyDetail = navController::navigateToPropertyDetail,
            commentsContent = { propertyId ->
                val commentsViewModel: CommentsViewModel = hiltViewModel()
                LaunchedEffect(propertyId) {
                    commentsViewModel.onAction(CommentsAction.Load(propertyId))
                }
                val commentsState by commentsViewModel.state.collectAsState()
                CommentSheetContent(
                    state = commentsState,
                    onAction = commentsViewModel::onAction
                )
            }
        )

        propertyAdditionGraph(onBackClick = navController::popBackStack)

        propertyDetailsScreen(onBackClick = navController::popBackStack)

        settingsGraph(
            onBackClick = navController::popBackStack,
            onLogoutClick = appState::signOut,
        )
    }
}
