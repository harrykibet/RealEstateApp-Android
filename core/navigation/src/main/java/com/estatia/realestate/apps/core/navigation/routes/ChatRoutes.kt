package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Utility

@Serializable
@Utility
data object ChatsRoute

@Serializable
@Utility
data object ChatsBaseRoute

@Serializable
@Utility
data class ChatDetailRoute(val chatId: String)
