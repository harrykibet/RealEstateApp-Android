package com.application.real_estate_app.feature_chats.data.db_entities

data class ChatsEntity(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val propertyId: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
