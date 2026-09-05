package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.annotations.Repository
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.domain.analytics.IAnalyticsTracker
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for orchestrating high-level engagement signals.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map domain actions (view, like, search) into structured [IAnalyticsTracker] events.
 * - Concurrency: Thread-safe; delegates to the [analyticsTracker] outbox.
 * - Performance: Minimal overhead; avoids heavy serialization on the calling thread.
 */
@Repository
@Singleton
internal class EngagementRepository @Inject constructor(
    private val analyticsTracker: IAnalyticsTracker
) : IEngagementRepository {

    override suspend fun reportMediaWatch(mediaId: String, watchTimeMs: Long, loopCount: Int) {
        analyticsTracker.logEvent(
            message = "Personalized media watch report",
            eventType = EventTypes.EVENT_PERSONALIZED_ENGAGEMENT,
            customMetadata = mapOf(
                "media_id" to mediaId,
                "watch_time_ms" to watchTimeMs.toString(),
                "loop_count" to loopCount.toString(),
                "signal_type" to "WATCH"
            )
        )
    }

    override suspend fun reportInteraction(mediaId: String, action: EngagementAction) {
        analyticsTracker.logEvent(
            message = "Personalized interaction report: ${action.name}",
            eventType = EventTypes.EVENT_PERSONALIZED_ENGAGEMENT,
            customMetadata = mapOf(
                "media_id" to mediaId,
                "action" to action.name,
                "signal_type" to "INTERACTION"
            )
        )
    }

    override suspend fun reportSearch(query: String, selectedPropertyId: String?) {
        analyticsTracker.logEvent(
            message = "Personalized search report",
            eventType = EventTypes.EVENT_PERSONALIZED_ENGAGEMENT,
            customMetadata = mapOf(
                "query" to query,
                "selected_id" to (selectedPropertyId ?: "none"),
                "signal_type" to "SEARCH"
            )
        )
    }
}
