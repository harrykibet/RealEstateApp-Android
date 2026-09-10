package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Serializable
@Helper
data object ChatsRoute

@Serializable
data object ChatsBaseRoute

@Serializable
@Helper
data class ChatDetailRoute(val chatId: String)
