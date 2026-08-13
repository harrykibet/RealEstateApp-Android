package com.estatia.realestate.apps.core.intelligence

import com.estatia.realestate.apps.core.domain.interfaces.IEngagementRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-level processor for aggregating and prioritizing user engagement signals
 * before they are shipped to the server-side recommendation engine.
 */
@Singleton
class EngagementSignalProcessor @Inject constructor(
    private val engagementRepository: IEngagementRepository
) {
    /**
     * Records a viewing session for a specific media item.
     * 
     * @param mediaId Unique identifier for the property video.
     * @param watchTimeMs Total duration the user watched the video.
     * @param loopCount Number of times the video automatically looped.
     */
    suspend fun processViewingSession(
        mediaId: String,
        watchTimeMs: Long,
        loopCount: Int
    ) {
        // Logic for local normalization or prioritization could go here.
        // For now, we delegate directly to the repository for shipping.
        engagementRepository.reportMediaWatch(
            mediaId = mediaId,
            watchTimeMs = watchTimeMs,
            loopCount = loopCount
        )
    }
}
