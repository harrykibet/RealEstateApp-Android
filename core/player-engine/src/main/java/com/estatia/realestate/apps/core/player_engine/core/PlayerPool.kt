package com.estatia.realestate.apps.core.player_engine.core

import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
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

    private var maxPoolSize = poolSizingPolicy.calculateMaxPoolSize()

    // accessOrder = true → LRU behavior
    private val players = LinkedHashMap<String, ManagedPlayer>(16, 0.75f, true)

    data class ManagedPlayer(
        val mediaId: String,
        val mediaType: MediaType,
        val player: ExoPlayer
    )

    fun get(mediaId: String): ManagedPlayer? = players[mediaId]

    /**
     * Returns an existing player or creates a new one using the
     * deterministic PlayerConfiguration snapshot.
     */
    fun getOrCreate(
        mediaId: String,
        mediaType: MediaType
    ): ManagedPlayer {

        // Return cached player if exists
        players[mediaId]?.let { return it }

        // Build deterministic configuration
        val config = configurationFactory.create(
            uri = mediaId.toUri(), // assuming mediaId = URL
            mediaType = mediaType
        )

        // Build player from configuration
        val player = playerFactory.create(config)

        val managed = ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = player
        )

        players[mediaId] = managed

        trimIfNeeded(excludeMediaId = null)
        return managed
    }

    fun forEachPlayer(block: (ExoPlayer, MediaType) -> Unit) {
        players.values.forEach { block(it.player, it.mediaType) }
    }

    fun markAccessed(mediaId: String) {
        players[mediaId] // access updates LRU order
    }

    fun release(mediaId: String) {
        players.remove(mediaId)?.player?.release()
    }

    fun releaseAll() {
        players.values.forEach { it.player.release() }
        players.clear()
    }

    fun updateMaxPoolSize(newSize: Int, activeMediaId: String?) {
        if (newSize == maxPoolSize) return

        if (newSize < maxPoolSize) {
            maxPoolSize = newSize
            trimIfNeeded(excludeMediaId = activeMediaId)
        } else {
            maxPoolSize = newSize
        }
    }

    fun trimIfNeeded(excludeMediaId: String?) {
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