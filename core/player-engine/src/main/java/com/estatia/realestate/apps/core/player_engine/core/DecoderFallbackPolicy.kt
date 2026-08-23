package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks decoder failures and decides when to force legacy decoders for specific media.
 * 🌡️ Bounded Failure Tracker: Cap at 50 to prevent unbounded memory growth in long sessions.
 */
@Singleton
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
        checkConfinement()
        return decoderFailures.containsKey(mediaId)
    }

    /**
     * Records a decoder failure for the specified media ID.
     */
    fun recordFailure(mediaId: String) {
        checkConfinement()
        decoderFailures.remove(mediaId)
        decoderFailures[mediaId] = true
    }

    /**
     * Clears failure history.
     */
    fun clear() {
        checkConfinement()
        decoderFailures.clear()
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("DecoderFallbackPolicy must only be accessed from the Main thread.")
        }
    }
}
