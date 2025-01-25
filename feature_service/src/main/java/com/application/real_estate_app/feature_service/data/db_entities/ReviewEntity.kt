package com.application.real_estate_app.feature_service.data.db_entities

data class ReviewEntity(
    val id: String? = null,
    val userId: String? = null,
    val serviceProviderId: String? = null,
    val rating: Float = 0f,
    val comment: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
