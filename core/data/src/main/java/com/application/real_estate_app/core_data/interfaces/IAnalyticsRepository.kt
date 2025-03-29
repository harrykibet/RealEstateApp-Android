package com.application.real_estate_app.core_data.interfaces

import com.application.real_estate_app.core_model.AnalyticsEvent

interface IAnalyticsRepository {
    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>? = null,
        onFailure: (Exception) -> Unit
    ): Boolean
    suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean
    suspend fun getEventsForUser(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<AnalyticsEvent>

    suspend fun getEventById(eventId: String, onFailure: (Exception) -> Unit): AnalyticsEvent?
    suspend fun generateEventId(): String
}