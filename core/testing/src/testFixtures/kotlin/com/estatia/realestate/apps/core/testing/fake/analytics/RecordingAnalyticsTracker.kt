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

    override suspend fun logEvent(event: AnalyticsEvent): AppResult<Unit> {
        witness.record(LoggedEvent.Domain(event))
        return AppResult.Success(Unit)
    }

    override suspend fun logEvent(message: String, eventType: String, customMetadata: Map<String, String>?): AppResult<Unit> {
        witness.record(LoggedEvent.Raw(message, eventType, customMetadata))
        return AppResult.Success(Unit)
    }

    override fun generateEventId(): String = java.util.UUID.randomUUID().toString()
}
