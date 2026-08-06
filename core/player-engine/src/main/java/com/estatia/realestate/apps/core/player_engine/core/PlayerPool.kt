package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import javax.inject.Inject
import javax.inject.Singleton

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
    private val idlePlayers = ArrayDeque<IdleManagedPlayer>()
    private val prewarmBudget: Int
        get() = maxOf(1, minOf(2, maxPoolSize / 2))

    data class ManagedPlayer(
        val mediaId: String,
        val mediaType: MediaType,
        val player: ExoPlayer,
        val analyticsListener: PlaybackAnalyticsListener,
        val reducer: PlaybackStateReducer = PlaybackStateReducer()
    )

    private data class IdleManagedPlayer(
        val player: ExoPlayer,
        val analyticsListener: PlaybackAnalyticsListener,
        val reducer: PlaybackStateReducer
    )

    fun get(mediaId: String): ManagedPlayer? {
        checkConfinement()
        return players[mediaId]
    }

    suspend fun getOrCreate(mediaId: String, uri: Uri, mediaType: MediaType): ManagedPlayer {
        checkConfinement()
        players[mediaId]?.let { return it }
        return prewarm(mediaId, uri, mediaType)
    }

    suspend fun prewarm(mediaId: String, uri: Uri, mediaType: MediaType): ManagedPlayer {
        checkConfinement()
        players[mediaId]?.let { return it }

        ensureIdlePlayers()

        val managed = idlePlayers.removeFirstOrNull()?.let { idle ->
            bindIdlePlayer(idle, mediaId, uri, mediaType)
        } ?: createManagedPlayer(mediaId, uri, mediaType)

        players[mediaId] = managed
        trimIfNeeded(excludeMediaId = null)
        return managed
    }

    private suspend fun ensureIdlePlayers() {
        while (idlePlayers.size < prewarmBudget) {
            val config = configurationFactory.create(uri = Uri.EMPTY, mediaType = MediaType.VOD)
            val created = playerFactory.create(config)
            created.player.clearMediaItems()
            created.player.playWhenReady = false
            created.player.stop()
            idlePlayers.addLast(
                IdleManagedPlayer(
                    player = created.player,
                    analyticsListener = created.analyticsListener,
                    reducer = PlaybackStateReducer()
                )
            )
        }
    }

    private suspend fun bindIdlePlayer(
        idle: IdleManagedPlayer,
        mediaId: String,
        uri: Uri,
        mediaType: MediaType
    ): ManagedPlayer {
        val config = configurationFactory.create(uri = uri, mediaType = mediaType)
        idle.player.clearMediaItems()
        idle.player.setMediaItem(config.mediaItem)
        idle.player.playWhenReady = false
        idle.player.prepare()
        return ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = idle.player,
            analyticsListener = idle.analyticsListener,
            reducer = idle.reducer
        )
    }

    private suspend fun createManagedPlayer(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType
    ): ManagedPlayer {
        val config = configurationFactory.create(uri = uri, mediaType = mediaType)
        val created = playerFactory.create(config)

        val managed = ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = created.player,
            analyticsListener = created.analyticsListener
        )

        managed.player.playWhenReady = false
        managed.player.prepare()
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
        players.remove(mediaId)?.let { managed ->
            managed.player.clearMediaItems()
            managed.player.stop()
            managed.analyticsListener.release()
            if (idlePlayers.size < prewarmBudget) {
                idlePlayers.addLast(
                    IdleManagedPlayer(
                        player = managed.player,
                        analyticsListener = managed.analyticsListener,
                        reducer = managed.reducer
                    )
                )
            } else {
                managed.player.release()
            }
        }
    }

    fun releaseAll() {
        checkConfinement()
        players.values.forEach { managed ->
            managed.player.clearMediaItems()
            managed.analyticsListener.release()
            managed.player.release()
        }
        players.clear()

        idlePlayers.forEach { idle ->
            idle.player.clearMediaItems()
            idle.analyticsListener.release()
            idle.player.release()
        }
        idlePlayers.clear()
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
            entry.value.player.clearMediaItems()
            entry.value.analyticsListener.release()
            entry.value.player.release()
            iterator.remove()
        }
    }
}
