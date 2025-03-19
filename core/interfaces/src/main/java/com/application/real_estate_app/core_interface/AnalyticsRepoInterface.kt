package com.application.real_estate_app.core_interface

interface AnalyticsRepoInterface {
    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>? = null,
        onFailure: (Exception) -> Unit
    ): Boolean
}