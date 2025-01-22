package com.application.real_estate_app.feature_analytics.domain.interfaces

import com.application.real_estate_app.core.data_utils.data_models.AnalyticsEvent

interface IAnalyticsApi {

    suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean
    suspend fun getEventsForUser(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<AnalyticsEvent>

    suspend fun getEventById(eventId: String, onFailure: (Exception) -> Unit): AnalyticsEvent?
    suspend fun generateEventId(): String
}
