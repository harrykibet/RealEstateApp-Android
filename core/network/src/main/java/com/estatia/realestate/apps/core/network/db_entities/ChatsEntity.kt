package com.estatia.realestate.apps.core.network.db_entities

data class ChatsEntity(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val propertyId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
