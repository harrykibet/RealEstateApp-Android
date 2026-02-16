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
import javax.inject.Inject

@UnstableApi
class PlayerFactory @Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val mediaSourceFactory: ProgressiveMediaSource.Factory,
    val liveStrategy: LivePlayerConfigurationStrategy,
    val vodStrategy: VodPlayerConfigurationStrategy,
    private val dynamicBitrateController: DynamicBitrateController,
    private val performanceOptimizer: PlayerPerformanceOptimizer,
    private val analyticsListener: PlaybackAnalyticsListener
) {

    /**
     * Creates a fully configured ExoPlayer instance using the provided strategy.
     *
     * Responsibilities:
     * - Apply base ExoPlayer configuration
     * - Apply Live/VOD strategy customization
     * - Apply performance optimizations
     * - Apply adaptive bitrate constraints
     * - Attach analytics listeners
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

        // Initial bitrate
        dynamicBitrateController.attach(player, mediaType)

        // Listen for environment changes
        networkUtils.registerListener {
            dynamicBitrateController.onEnvironmentChanged(player, mediaType)
        }

        batteryManager.registerListener {
            dynamicBitrateController.onEnvironmentChanged(player, mediaType)
        }

        return player
    }
}


