package com.estatia.realestate.apps.core.network.db_entities

data class ReviewEntity(
    val id: String? = null,
    val userId: String? = null,
    val serviceProviderId: String? = null,
    val rating: Float = 0f,
    val comment: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
