package com.estatia.realestate.apps.core.testing.fake.analytics

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.analytics.IAnalyticsTracker
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.testing.witness.Witness

/**
 * A fake analytics tracker that records all events for later verification.
 */
class RecordingAnalyticsTracker : IAnalyticsTracker {
    
    val witness = Witness<LoggedEvent>()

    sealed interface LoggedEvent {
        data class Domain(val event: AnalyticsEvent) : LoggedEvent
        data class Raw(val message: String, val type: String, val metadata: Map<String, String>?) : LoggedEvent
    }

    override suspend fun logEvent(event: AnalyticsEvent) {
        witness.record(LoggedEvent.Domain(event))
    }

    override suspend fun logEvent(message: String, eventType: String, customMetadata: Map<String, String>?) {
        witness.record(LoggedEvent.Raw(message, eventType, customMetadata))
    }

    override fun generateEventId(): String = java.util.UUID.randomUUID().toString()

    override suspend fun syncEvents(): AppResult<Unit> = AppResult.Success(Unit)
}
