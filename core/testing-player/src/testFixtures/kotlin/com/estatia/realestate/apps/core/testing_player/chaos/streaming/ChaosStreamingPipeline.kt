package com.estatia.realestate.apps.core.testing_player.chaos.streaming
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority

/**
 * Adversarial implementation of [IStreamingPipeline] for testing QoS and segment failure resilience.
 */
@UnstableApi
class ChaosStreamingPipeline(
    private val delegate: IStreamingPipeline
) : IStreamingPipeline by delegate {

    private var shouldFailSegment = false

    fun setFailSegments(enabled: Boolean) {
        shouldFailSegment = enabled
    }

    override fun warm(mediaId: String, uri: MediaReference, priority: WarmPriority, qualityHint: String?) {
        if (!shouldFailSegment) {
            delegate.warm(mediaId, uri, priority, qualityHint)
        }
        // If segments should fail, we simply skip warming to simulate a stalled buffer
    }
}
