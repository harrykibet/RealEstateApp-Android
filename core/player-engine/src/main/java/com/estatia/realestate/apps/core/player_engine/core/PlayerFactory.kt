package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.perfomance.PlayerPerformanceOptimizer
import com.estatia.realestate.apps.core.player_engine.strategies.LivePlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.strategies.VodPlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.utils.DynamicBitrateController
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerFactory @Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val mediaSourceFactory: ProgressiveMediaSource.Factory,
    val liveStrategy: LivePlayerConfigurationStrategy,
    val vodStrategy: VodPlayerConfigurationStrategy,
    private val dynamicBitrateController: DynamicBitrateController,
    private val performanceOptimizer: PlayerPerformanceOptimizer,
    private val analyticsListener: PlaybackAnalyticsListener
) {

    /**
     * Create a configured ExoPlayer instance with dynamic ABR support
     */
    fun create(strategy: PlayerConfigurationStrategy): ExoPlayer {
        val baseBuilder = ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(mediaSourceFactory)

        val configuredBuilder = strategy.configure(context, baseBuilder)
        performanceOptimizer.optimize(configuredBuilder)

        val player = configuredBuilder.build()
        player.addAnalyticsListener(analyticsListener)

        val mediaType = when (strategy) {
            is LivePlayerConfigurationStrategy -> MediaType.LIVE
            is VodPlayerConfigurationStrategy -> MediaType.VOD
            else -> MediaType.VOD
        }

        // Initial dynamic bitrate attachment
        dynamicBitrateController.attach(player, mediaType)

        return player
    }
}
