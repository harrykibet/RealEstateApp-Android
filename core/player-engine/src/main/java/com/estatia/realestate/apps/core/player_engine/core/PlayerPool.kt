package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerPool @Inject constructor(
    private val playerFactory: PlayerFactory,
    private val configurationFactory: IPlayerConfigurationFactory,
    private val analyticsListenerProvider: Provider<PlaybackAnalyticsListener>,
    poolSizingPolicy: IPlayerPoolSizingPolicy
) {
    private val confinementThread: Thread = Looper.getMainLooper().thread

    private fun checkConfinement() {
        check(Thread.currentThread() === confinementThread) {
            "PlayerPool must only be accessed from the player dispatcher thread (Main). " +
                    "Called from ${Thread.currentThread().name}, expected ${confinementThread.name}."
        }
    }

    private val poolUpdates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var maxPoolSize = poolSizingPolicy.calculateMaxPoolSize()
    private val players = LinkedHashMap<String, ManagedPlayer>(16, 0.75f, true)
    private val idlePlayers = ArrayDeque<IdleManagedPlayer>()
    private val prewarmBudget: Int
        get() = maxOf(1, minOf(2, maxPoolSize / 2))

    private data class IdleManagedPlayer(
        val player: ExoPlayer,
        val reducer: PlaybackStateReducer
    )

    fun get(mediaId: String): ManagedPlayer? {
        checkConfinement()
        return players[mediaId]
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeMediaState(mediaId: String): Flow<PlaybackStateReducer.State> {
        return poolUpdates.onStart { emit(Unit) }
            .flatMapLatest {
                players[mediaId]?.reducer?.state ?: flow { emit(PlaybackStateReducer.State.Idle) }
            }
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
        poolUpdates.tryEmit(Unit)
        trimIfNeeded(excludeMediaId = null)
        return managed
    }

    private suspend fun ensureIdlePlayers() {
        while (idlePlayers.size < prewarmBudget) {
            val config = configurationFactory.create("idle_${System.currentTimeMillis()}", Uri.EMPTY, MediaType.VOD)
            val created = playerFactory.create(config)
            
            // Immediately detach listener for pooling
            created.player.removeAnalyticsListener(created.analyticsListener)
            created.analyticsListener.release()
            
            created.player.clearMediaItems()
            created.player.playWhenReady = false
            created.player.stop()
            idlePlayers.addLast(
                IdleManagedPlayer(
                    player = created.player,
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
        val config = configurationFactory.create(mediaId, uri, mediaType)
        val listener = analyticsListenerProvider.get()
        
        idle.player.addAnalyticsListener(listener)
        idle.player.clearMediaItems()
        idle.player.setMediaItem(config.mediaItem)
        idle.player.playWhenReady = false
        idle.player.prepare()
        
        return ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = idle.player,
            analyticsListener = listener,
            reducer = idle.reducer
        )
    }

    private suspend fun createManagedPlayer(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType
    ): ManagedPlayer {
        val config = configurationFactory.create(mediaId, uri, mediaType)
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
            // Detach and kill analytics scope on recycle
            managed.player.removeAnalyticsListener(managed.analyticsListener)
            managed.analyticsListener.release()
            
            managed.player.clearMediaItems()
            managed.player.stop()
            
            if (idlePlayers.size < prewarmBudget) {
                idlePlayers.addLast(
                    IdleManagedPlayer(
                        player = managed.player,
                        reducer = managed.reducer
                    )
                )
            } else {
                managed.player.release()
            }
            poolUpdates.tryEmit(Unit)
        }
    }

    fun releaseAll() {
        checkConfinement()
        players.values.forEach { managed ->
            managed.player.removeAnalyticsListener(managed.analyticsListener)
            managed.analyticsListener.release()
            managed.player.clearMediaItems()
            managed.player.release()
        }
        players.clear()

        idlePlayers.forEach { idle ->
            idle.player.clearMediaItems()
            idle.player.release()
        }
        idlePlayers.clear()
        poolUpdates.tryEmit(Unit)
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
            
            entry.value.player.removeAnalyticsListener(entry.value.analyticsListener)
            entry.value.analyticsListener.release()
            entry.value.player.clearMediaItems()
            entry.value.player.release()
            iterator.remove()
        }
        poolUpdates.tryEmit(Unit)
    }
}
