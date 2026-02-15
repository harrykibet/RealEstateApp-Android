package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.perfomance.PlayerPerformanceOptimizer
import com.estatia.realestate.apps.core.player_engine.strategies.LivePlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.strategies.VodPlayerConfigurationStrategy
import javax.inject.Inject

@UnstableApi
class ExoPlayerFactory @Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val mediaSourceFactory: ProgressiveMediaSource.Factory,
    val liveStrategy: LivePlayerConfigurationStrategy,  // <-- public
    val vodStrategy: VodPlayerConfigurationStrategy,    // <-- public
    private val performanceOptimizer: PlayerPerformanceOptimizer,
    private val analyticsListener: PlaybackAnalyticsListener
) {

    /**
     * Creates a configured ExoPlayer instance using the provided strategy.
     *
     * @param strategy The PlayerConfigurationStrategy to apply (e.g., Live or VOD)
     * @return Configured ExoPlayer instance ready to attach to a view.
     */
    fun create(strategy: PlayerConfigurationStrategy): ExoPlayer {

        // Start with a base builder
        val baseBuilder = ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(mediaSourceFactory)

        // Apply the strategy configuration
        val configuredBuilder = strategy.configure(context, baseBuilder)

        // Apply device performance optimizations
        performanceOptimizer.optimize(configuredBuilder)

        // Build the player and attach analytics listener
        return configuredBuilder.build().also {
            it.addAnalyticsListener(analyticsListener)
        }
    }
}
