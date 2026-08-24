package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser
import kotlinx.datetime.Instant

object ChatFixtures {
    fun user(id: String = "user_1", name: String = "User One") = ChatUser(
        id = id,
        name = name,
        profilePictureUrl = null,
        isActive = true
    )

    fun chat(id: String = "chat_1", lastMessage: String = "Hello") = Chat(
        id = id,
        user = user(),
        lastMessage = lastMessage,
        lastMessageTimestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
        unreadCount = 0
    )
}
