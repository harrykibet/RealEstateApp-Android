package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import android.os.Looper
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

// PlayerFactory.kt — request a fresh listener instance per player via Provider<T>
@UnstableApi
@Singleton
internal class PlayerFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val analyticsListenerProvider: Provider<PlaybackAnalyticsListener>
) {
    data class CreatedPlayer(
        val player: ExoPlayer,
        val analyticsListener: PlaybackAnalyticsListener
    )

    fun create(configuration: PlayerConfiguration): CreatedPlayer {
        val builder = ExoPlayer.Builder(context)
            .setLooper(Looper.getMainLooper())
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(configuration.mediaSourceFactory)
            .setLoadControl(configuration.loadControl)

        configuration.livePlaybackSpeedControl?.let { builder.setLivePlaybackSpeedControl(it) }

        val player = builder.build()
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.setMediaItem(configuration.mediaItem)

        val listener = analyticsListenerProvider.get()
        player.addAnalyticsListener(listener)

        return CreatedPlayer(player, listener)
    }
}
