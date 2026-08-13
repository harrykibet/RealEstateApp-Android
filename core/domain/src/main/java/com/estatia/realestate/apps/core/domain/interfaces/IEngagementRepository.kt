package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.model.engagement.EngagementAction

/**
 * Domain-level contract for reporting personalized engagement signals.
 * These signals are consumed by the recommendation engine to refine user feeds.
 */
interface IEngagementRepository {
    /**
     * Reports a viewing session for a media item.
     */
    suspend fun reportMediaWatch(
        mediaId: String,
        watchTimeMs: Long,
        loopCount: Int
    )

    /**
     * Reports a discrete interaction with a property listing.
     */
    suspend fun reportInteraction(
        mediaId: String,
        action: EngagementAction
    )

    /**
     * Reports a search query and any resulting selection.
     */
    suspend fun reportSearch(
        query: String,
        selectedPropertyId: String? = null
    )
}
