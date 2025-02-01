package com.application.real_estate_app.feature_mediaplayer.core

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.BandwidthMeter
import javax.inject.Inject
import javax.inject.Singleton

// Player pooling/reuse
@OptIn(UnstableApi::class)
@Singleton
class ExoPlayerInstanceManager
@Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter
) {
    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()

    fun acquirePlayer(mediaId: String): ExoPlayer {
        return playerPool.find { !it.isPlaying }?.also {
            activePlayers[mediaId] = it
        } ?: createNewPlayer().also {
            activePlayers[mediaId] = it
        }
    }

    private fun createNewPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .build()
            .also { playerPool.add(it) }
    }

    fun releasePlayer(mediaId: String) {
        activePlayers[mediaId]?.stop()
        activePlayers.remove(mediaId)
    }
}