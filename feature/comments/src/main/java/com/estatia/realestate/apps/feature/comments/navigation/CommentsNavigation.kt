package com.estatia.realestate.apps.feature.comments.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.navArgument
import com.estatia.realestate.apps.feature.comments.ui.routes.CommentsRoute

fun NavGraphBuilder.commentsGraph(
    onBackClick: () -> Unit
) {
    navigation(
        route = CommentsRoutes.GRAPH,
        startDestination = "${CommentsRoutes.COMMENTS}/{propertyId}"
    ) {

        composable(
            route = "${CommentsRoutes.COMMENTS}/{propertyId}",
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId =
                backStackEntry.arguments?.getString("propertyId")
                    ?: return@composable

            CommentsRoute(
                propertyId = propertyId,
                onBack = onBackClick
            )
        }
    }
}
