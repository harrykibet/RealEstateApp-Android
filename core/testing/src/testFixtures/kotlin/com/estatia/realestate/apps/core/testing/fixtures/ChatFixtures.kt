package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.feature.Chat
import com.estatia.realestate.apps.core.model.feature.ChatUser
import kotlinx.datetime.Instant
import java.util.UUID

/**
 * Unified source of truth for chat domain fixtures.
 */
object ChatFixtures {

    /**
     * Returns a deterministic chat user.
     */
    fun defaultUser() = ChatUser(
        id = "user_1",
        name = "User One",
        profilePictureUrl = "https://example.com/alice.jpg",
        isActive = true
    )

    /**
     * Returns a deterministic chat object.
     */
    fun defaultChat() = Chat(
        id = "chat_1",
        user = defaultUser(),
        lastMessage = "Hello from Estatia!",
        lastMessageTimestamp = Instant.fromEpochMilliseconds(1746619200000),
        unreadCount = 1
    )

    /**
     * Factory method for building customized or randomized chat users.
     */
    fun buildUser(
        id: String = UUID.randomUUID().toString(),
        name: String = "Generated User"
    ) = defaultUser().copy(
        id = id,
        name = name
    )

    /**
     * Factory method for building customized or randomized chat objects.
     */
    fun buildChat(
        id: String = UUID.randomUUID().toString(),
        lastMessage: String = "Random message"
    ) = defaultChat().copy(
        id = id,
        lastMessage = lastMessage,
        lastMessageTimestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis())
    )

    @Deprecated("Use defaultUser() or buildUser()", ReplaceWith("defaultUser()"))
    fun user(id: String = "user_1", name: String = "User One") = buildUser(id, name)

    @Deprecated("Use defaultChat() or buildChat()", ReplaceWith("defaultChat()"))
    fun chat(id: String = "chat_1", lastMessage: String = "Hello") = buildChat(id, lastMessage)
}
