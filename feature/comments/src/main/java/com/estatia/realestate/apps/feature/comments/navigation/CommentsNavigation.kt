package com.estatia.realestate.apps.feature.comments.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.estatia.realestate.apps.feature.comments.ui.routes.CommentsRoute as CommentsRouteScreen

fun NavController.navigateToComments(propertyId: String, navOptions: NavOptions? = null) =
    navigate(route = CommentsRoute(propertyId), navOptions)

fun NavGraphBuilder.commentsGraph(
    onBackClick: () -> Unit
) {
    navigation<CommentsBaseRoute>(
        startDestination = CommentsRoute::class
    ) {

        composable<CommentsRoute> { backStackEntry ->
            val route: CommentsRoute = backStackEntry.toRoute()

            CommentsRouteScreen(
                propertyId = route.propertyId,
                onBack = onBackClick
            )
        }
    }
}
