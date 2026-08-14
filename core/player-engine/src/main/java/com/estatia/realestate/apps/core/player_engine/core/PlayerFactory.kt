package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import android.os.Looper
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.player_engine.advanced.SpatialAudioRenderer
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

// PlayerFactory.kt — request a fresh listener instance per player via Provider<T>
@UnstableApi
@Singleton
class PlayerFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val analyticsListenerProvider: Provider<PlaybackAnalyticsListener>,
    private val spatialAudioRendererProvider: Provider<SpatialAudioRenderer>
) {
    data class CreatedPlayer(
        val player: ExoPlayer,
        val analyticsListener: PlaybackAnalyticsListener
    )

    fun create(configuration: PlayerConfiguration): CreatedPlayer {
        val spatialAudioRenderer = spatialAudioRendererProvider.get()
        spatialAudioRenderer.logSpatialAudioStatus()

        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): AudioSink? {
                return DefaultAudioSink.Builder(context)
                    .build()
                    .apply { setListener(spatialAudioRenderer) }
            }
        }

        val builder = ExoPlayer.Builder(context, renderersFactory)
            .setLooper(Looper.getMainLooper())
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(configuration.mediaSourceFactory)
            .setLoadControl(configuration.loadControl)

        configuration.livePlaybackSpeedControl?.let { builder.setLivePlaybackSpeedControl(it) }

        val player = builder.build()
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        player.trackSelectionParameters = configuration.trackSelectionParameters
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.setMediaItem(configuration.mediaItem)

        val listener = analyticsListenerProvider.get()
        player.addAnalyticsListener(listener)

        return CreatedPlayer(player, listener)
    }

    /**
     * Creates a lightweight idle player without media metadata or source configuration.
     * Used for warming the pool without the overhead of CDN resolution.
     */
    fun createIdle(): ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        }

        val player = ExoPlayer.Builder(context, renderersFactory)
            .setLooper(Looper.getMainLooper())
            .setBandwidthMeter(bandwidthMeter)
            .build()

        player.repeatMode = Player.REPEAT_MODE_ONE
        player.playWhenReady = false

        return player
    }
}
