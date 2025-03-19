package com.application.real_estate_app.core_notifications.data.entities

data class NotificationEntity(
    val notificationId: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
