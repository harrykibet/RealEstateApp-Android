package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent

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
}
