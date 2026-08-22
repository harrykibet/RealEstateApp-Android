package com.estatia.realestate.apps.core.domain.analytics

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.common.exceptions.AppResult

/**
 * Interface for tracking analytics and performance events.
 */
interface IAnalyticsTracker {

    /**
     * Logs a message-based event with optional metadata.
     */
    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>? = null
    )

    /**
     * Logs a structured [AnalyticsEvent].
     */
    suspend fun logEvent(
        event: AnalyticsEvent
    )

    /**
     * Generates a unique ID for grouping events into a single session.
     */
    fun generateEventId(): String

    /**
     * Attempts to sync any pending events from the local outbox.
     */
    suspend fun syncEvents(): AppResult<Unit>
}
