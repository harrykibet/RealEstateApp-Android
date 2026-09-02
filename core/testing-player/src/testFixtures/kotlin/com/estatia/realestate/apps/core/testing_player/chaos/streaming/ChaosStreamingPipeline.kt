package com.estatia.realestate.apps.core.testing_player.chaos.streaming
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Adversarial implementation of [IStreamingPipeline] for testing QoS and segment failure resilience.
 */
@UnstableApi
class ChaosStreamingPipeline(
    private val delegate: IStreamingPipeline
) : IStreamingPipeline by delegate {

    private val shouldFailSegment = AtomicBoolean(false)

    fun setFailSegments(enabled: Boolean) {
        shouldFailSegment.set(enabled)
    }

    override fun warm(mediaId: String, uri: MediaReference, priority: WarmPriority, qualityHint: String?) {
        if (!shouldFailSegment.get()) {
            delegate.warm(mediaId, uri, priority, qualityHint)
        }
        // If segments should fail, we simply skip warming to simulate a stalled buffer
    }
}
