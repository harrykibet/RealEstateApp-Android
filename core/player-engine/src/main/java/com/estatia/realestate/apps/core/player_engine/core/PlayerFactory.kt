package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.perfomance.PlayerPerformanceOptimizer
import com.estatia.realestate.apps.core.player_engine.strategies.LivePlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.strategies.VodPlayerConfigurationStrategy
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerFactory @Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    val liveStrategy: LivePlayerConfigurationStrategy,
    val vodStrategy: VodPlayerConfigurationStrategy,
    private val performanceOptimizer: PlayerPerformanceOptimizer,
    private val analyticsListener: PlaybackAnalyticsListener
) {

    /**
     * Create a configured ExoPlayer instance
     */
    fun create(strategy: PlayerConfigurationStrategy): ExoPlayer {
        val baseBuilder = ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)

        val configuredBuilder = strategy.configure(context, baseBuilder)
        performanceOptimizer.optimize(configuredBuilder)

        val player = configuredBuilder.build()
        player.addAnalyticsListener(analyticsListener)

        return player
    }
}
