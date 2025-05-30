package com.estatia.realestate.apps.core.network.db_entities

data class NotificationEntity(
    val notificationId: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
