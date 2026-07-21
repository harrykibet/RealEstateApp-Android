package com.estatia.realestate.apps.core.player_engine.core

import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import javax.inject.Inject
import javax.inject.Singleton

// PlayerPool.kt
@UnstableApi
@Singleton
class PlayerPool @Inject constructor(
    private val playerFactory: PlayerFactory,
    private val configurationFactory: IPlayerConfigurationFactory,
    poolSizingPolicy: IPlayerPoolSizingPolicy
) {
    private val confinementThread: Thread by lazy { Thread.currentThread() }

    private fun checkConfinement() {
        check(Thread.currentThread() === confinementThread) {
            "PlayerPool must only be accessed from the player dispatcher thread. " +
                    "Called from ${Thread.currentThread().name}, expected ${confinementThread.name}."
        }
    }

    private var maxPoolSize = poolSizingPolicy.calculateMaxPoolSize()

    private val players = LinkedHashMap<String, ManagedPlayer>(16, 0.75f, true)

    data class ManagedPlayer(
        val mediaId: String,
        val mediaType: MediaType,
        val player: ExoPlayer,
        val analyticsListener: PlaybackAnalyticsListener,
        val reducer: PlaybackStateReducer = PlaybackStateReducer()
    )

    fun get(mediaId: String): ManagedPlayer? {
        checkConfinement()
        return players[mediaId]
    }

    suspend fun getOrCreate(mediaId: String, mediaType: MediaType): ManagedPlayer {
        checkConfinement()

        players[mediaId]?.let { return it }

        val config = configurationFactory.create(uri = mediaId.toUri(), mediaType = mediaType)
        val created = playerFactory.create(config)

        val managed = ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = created.player,
            analyticsListener = created.analyticsListener
        )

        players[mediaId] = managed
        trimIfNeeded(excludeMediaId = null)
        return managed
    }

    fun forEachPlayer(block: (ExoPlayer, MediaType) -> Unit) {
        checkConfinement()
        players.values.forEach { block(it.player, it.mediaType) }
    }

    fun markAccessed(mediaId: String) {
        checkConfinement()
        players[mediaId]
    }

    fun release(mediaId: String) {
        checkConfinement()
        players.remove(mediaId)?.player?.release()
    }

    fun releaseAll() {
        checkConfinement()
        players.values.forEach { it.player.release() }
        players.clear()
    }

    fun updateMaxPoolSize(newSize: Int, activeMediaId: String?) {
        checkConfinement()
        if (newSize == maxPoolSize) return
        if (newSize < maxPoolSize) {
            maxPoolSize = newSize
            trimIfNeeded(excludeMediaId = activeMediaId)
        } else {
            maxPoolSize = newSize
        }
    }

    fun trimIfNeeded(excludeMediaId: String?) {
        checkConfinement()
        if (players.size <= maxPoolSize) return

        val iterator = players.entries.iterator()
        while (iterator.hasNext() && players.size > maxPoolSize) {
            val entry = iterator.next()
            if (entry.key == excludeMediaId) continue
            entry.value.player.release()
            iterator.remove()
        }
    }
}