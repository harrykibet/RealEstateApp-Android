package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent

interface IAnalyticsRemoteDataSource {

    suspend fun logEvent(
        event: AnalyticsEvent
    ): Result<Unit>

    suspend fun getEventsForUser(
        userId: String
    ): Result<List<AnalyticsEvent>>

    suspend fun getEventById(
        eventId: String
    ): Result<AnalyticsEvent?>

    suspend fun generateEventId(): Result<String>

    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?
    ): Result<Unit>
}