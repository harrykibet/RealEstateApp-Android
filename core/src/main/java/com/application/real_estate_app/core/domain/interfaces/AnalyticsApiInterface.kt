package com.application.real_estate_app.core.domain.interfaces

interface AnalyticsApiInterface {
    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>? = null,
        onFailure: (Exception) -> Unit
    ): Boolean
}