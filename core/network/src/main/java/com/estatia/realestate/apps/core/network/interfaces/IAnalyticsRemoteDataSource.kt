package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface IAnalyticsRemoteDataSource {


    suspend fun logEvent(
        event: AnalyticsEvent
    ): AppResult<Unit>


    @Deprecated("Raw event querying is no longer supported on the client. Use BigQuery export.")
    suspend fun getEventsForUser(
        userId: String
    ): AppResult<List<AnalyticsEvent>>


    @Deprecated("Raw event querying is no longer supported on the client. Use BigQuery export.")
    suspend fun getEventById(
        eventId: String
    ): AppResult<AnalyticsEvent?>


    fun generateEventId(): String


    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?
    ): AppResult<Unit>
}
