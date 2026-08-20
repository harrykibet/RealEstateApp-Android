package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
data object ChatsRoute

@Serializable
data object ChatsBaseRoute

@Serializable
data class ChatDetailRoute(val chatId: String)
