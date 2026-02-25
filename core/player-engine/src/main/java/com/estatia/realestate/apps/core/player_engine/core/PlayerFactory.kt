package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val analyticsListener: PlaybackAnalyticsListener
) {

    /**
     * Creates a fully configured ExoPlayer instance from an immutable
     * PlayerConfiguration snapshot.
     *
     * No mutation. No branching. No strategy pattern.
     */
    fun create(configuration: PlayerConfiguration): ExoPlayer {

        val builder = ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(configuration.mediaSourceFactory)
            .setLoadControl(configuration.loadControl)

        configuration.livePlaybackSpeedControl?.let {
            builder.setLivePlaybackSpeedControl(it)
        }

        val player = builder.build()

        player.setMediaItem(configuration.mediaItem)
        player.addAnalyticsListener(analyticsListener)

        return player
    }
}