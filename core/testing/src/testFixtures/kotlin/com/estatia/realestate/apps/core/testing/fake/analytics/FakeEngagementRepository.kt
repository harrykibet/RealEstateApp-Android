package com.estatia.realestate.apps.core.testing.fake.analytics

import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import com.estatia.realestate.apps.core.testing.witness.Witness

/**
 * A fake engagement repository that records signals via [Witness].
 */
class FakeEngagementRepository : IEngagementRepository {

    val witness = Witness<EngagementSignal>()

    sealed interface EngagementSignal {
        data class Watch(val mediaId: String, val timeMs: Long, val loops: Int) : EngagementSignal
        data class Interaction(val mediaId: String, val action: EngagementAction) : EngagementSignal
        data class Search(val query: String, val selectedId: String?) : EngagementSignal
    }

    override suspend fun reportMediaWatch(mediaId: String, watchTimeMs: Long, loopCount: Int) {
        witness.record(EngagementSignal.Watch(mediaId, watchTimeMs, loopCount))
    }

    override suspend fun reportInteraction(mediaId: String, action: EngagementAction) {
        witness.record(EngagementSignal.Interaction(mediaId, action))
    }

    override suspend fun reportSearch(query: String, selectedPropertyId: String?) {
        witness.record(EngagementSignal.Search(query, selectedPropertyId))
    }
}
