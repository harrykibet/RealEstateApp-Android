package com.estatia.realestate.apps.core.network.db_entities
import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

@EntityModel
data class NotificationEntity(
    val notificationId: String,
    val userId: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
