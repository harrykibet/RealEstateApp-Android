package com.estatia.realestate.apps.feature.player.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.estatia.realestate.apps.feature.player.core.ExoPlayerInstanceManager
import com.estatia.realestate.apps.feature.player.route.PlayerRoute

fun NavGraphBuilder.playerGraph(
    onBackClick: () -> Unit,
) {
    composable(
        route = PlayerDestination.routeWithArgs,
        arguments = listOf(
            navArgument(PlayerDestination.mediaIdArg) {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val mediaId =
            backStackEntry.arguments
                ?.getString(PlayerDestination.mediaIdArg)
                ?: return@composable

        PlayerRoute(
            mediaId = mediaId,
            exoplayer = ExoPlayerInstanceManager,
            onBackClick = onBackClick
        )
    }
}
