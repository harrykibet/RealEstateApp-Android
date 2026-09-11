package com.estatia.realestate.apps.core.player_engine.core

import com.estatia.realestate.apps.core.common.concurrency.Confinement
import javax.inject.Inject
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Policy

/**
 * Tracks decoder failures and decides when to force legacy decoders for specific media.
 * 🌡️ Bounded Failure Tracker: Cap at 50 to prevent unbounded memory growth in long sessions.
 */
@Singleton
@Policy
class DecoderFallbackPolicy @Inject constructor() {
    
    private val decoderFailures = object : LinkedHashMap<String, Boolean>(50, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Boolean>?): Boolean {
            return size > 50
        }
    }

    /**
     * Returns true if the specified media ID has previously failed with a decoder error.
     */
    fun shouldForceLegacy(mediaId: String): Boolean {
        Confinement.checkMainThread()
        return decoderFailures.containsKey(mediaId)
    }

    /**
     * Records a decoder failure for the specified media ID.
     */
    fun recordFailure(mediaId: String) {
        Confinement.checkMainThread()
        decoderFailures.remove(mediaId)
        decoderFailures[mediaId] = true
    }

    /**
     * Clears failure history.
     */
    fun clear() {
        Confinement.checkMainThread()
        decoderFailures.clear()
    }
}
