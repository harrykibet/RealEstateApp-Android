package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent

interface IAnalyticsTracker {

    suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>? = null
    )

    suspend fun logEvent(
        event: AnalyticsEvent
    )

    fun generateEventId(): String
}
