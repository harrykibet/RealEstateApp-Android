package com.estatia.realestate.apps.feature.chats.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.core.navigation.routes.ChatsBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.ChatsRoute
import com.estatia.realestate.apps.feature.chats.ui.ChatsRoute as ChatsScreen

fun NavController.navigateToChats(navOptions: NavOptions? = null) =
    navigate(route = ChatsRoute, navOptions)

fun NavGraphBuilder.chatsGraph(
    onChatClick: (String) -> Unit
) {
    navigation<ChatsBaseRoute>(startDestination = ChatsRoute) {
        composable<ChatsRoute> {
            ChatsScreen(
                onChatClick = onChatClick
            )
        }
    }
}
