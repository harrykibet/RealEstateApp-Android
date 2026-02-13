package com.estatia.realestate.apps.feature.player.navigation

object PlayerDestination {
    const val route = "player"
    const val mediaIdArg = "mediaId"

    const val routeWithArgs = "$route/{$mediaIdArg}"

    fun createRoute(mediaId: String): String {
        return "$route/$mediaId"
    }
}