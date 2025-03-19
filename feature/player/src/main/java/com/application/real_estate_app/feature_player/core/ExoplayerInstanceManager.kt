package com.application.real_estate_app.feature_player.core

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.ui.PlayerView
import com.application.real_estate_app.core_interface.IExoplayer
import com.application.real_estate_app.feature_player.streaming.ContentPreloader
import javax.inject.Inject
import javax.inject.Singleton

// Player pooling/reuse
@OptIn(UnstableApi::class)
@Singleton
class ExoPlayerInstanceManager
@Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val contentPreloader: ContentPreloader
): IExoplayer {
    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()

    override fun acquirePlayer(mediaId: String): ExoPlayer {
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

    override fun releasePlayer(mediaId: String) {
        activePlayers[mediaId]?.stop()
        activePlayers.remove(mediaId)
    }

    override fun preloadMedia(mediaId: String) {
        contentPreloader.schedulePreload(mediaId)
    }

    override fun attachPlayerToView(playerView: PlayerView, mediaId: String) {
        TODO("Not yet implemented")
    }

    override fun detachPlayer() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }
}