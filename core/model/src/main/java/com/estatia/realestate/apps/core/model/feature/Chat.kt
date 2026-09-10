package com.estatia.realestate.apps.core.model.feature

import kotlin.time.Instant
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class Chat(
    val id: String,
    val user: ChatUser,
    val lastMessage: String,
    val lastMessageTimestamp: Instant,
    val unreadCount: Int = 0
)

@DomainModel
data class ChatUser(
    val id: String,
    val name: String,
    val profilePictureUrl: String?,
    val isActive: Boolean = false
)

@DomainModel
data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Instant
)
