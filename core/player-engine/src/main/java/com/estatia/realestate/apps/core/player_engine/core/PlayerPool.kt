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
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerPool @Inject constructor(
    private val playerFactory: PlayerFactory,
    private val configurationFactory: IPlayerConfigurationFactory,
    private val analyticsListenerProvider: Provider<PlaybackAnalyticsListener>,
    private val environmentCoordinator: EnvironmentCoordinator,
    @param:EngineScope private val scope: CoroutineScope,
    poolSizingPolicy: IPlayerPoolSizingPolicy
) {
    private val confinementThread: Thread = Looper.getMainLooper().thread
    private val inFlightCreations = mutableMapOf<String, CompletableDeferred<ManagedPlayer>>()

    private fun checkConfinement() {
        check(Thread.currentThread() === confinementThread) {
            "PlayerPool must only be accessed from the player dispatcher thread (Main). " +
                    "Called from ${Thread.currentThread().name}, expected ${confinementThread.name}."
        }
    }

    private val poolUpdates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var maxPoolSize = poolSizingPolicy.calculateMaxPoolSize(environmentCoordinator.environment.value)
    private val players = LinkedHashMap<String, ManagedPlayer>(16, 0.75f, true)
    private val idlePlayers = ArrayDeque<ExoPlayer>()
    private val prewarmBudget: Int
        get() = if (maxPoolSize <= 1) 0 else maxOf(1, minOf(2, maxPoolSize / 2))

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

    suspend fun getOrCreate(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        forceLegacy: Boolean = false,
        title: String? = null,
        artist: String? = null
    ): ManagedPlayer {
        checkConfinement()
        // If forcing legacy, we should probably re-prepare even if it's in the pool
        players[mediaId]?.let {
            if (forceLegacy) release(mediaId) else return it
        }
        return prewarm(mediaId, uri, mediaType, forceLegacy, urgent = true, title = title, artist = artist)
    }

    suspend fun prewarm(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        forceLegacy: Boolean = false,
        urgent: Boolean = false,
        title: String? = null,
        artist: String? = null
    ): ManagedPlayer {
        checkConfinement()
        
        // 1. Check if already active
        players[mediaId]?.let { return it }

        // 2. Check if creation is already in-flight for this specific ID
        val deferred = inFlightCreations[mediaId]
        if (deferred != null) {
            return deferred.await()
        }

        // 3. Start a new creation task
        val newDeferred = CompletableDeferred<ManagedPlayer>()
        inFlightCreations[mediaId] = newDeferred

        return try {
            ensureIdlePlayers()

            val managed = idlePlayers.removeFirstOrNull()?.let { player ->
                bindIdlePlayer(player, mediaId, uri, mediaType, forceLegacy, title, artist)
            } ?: createManagedPlayer(mediaId, uri, mediaType, forceLegacy, title, artist)

            // 🏎️ Late-bound Capacity Check:
            // If the pool shrunk while we were building this player, and we're at or over capacity,
            // immediately release this instance unless it's an urgent request (active playback).
            if (!urgent && players.size >= maxPoolSize && !players.containsKey(mediaId)) {
                managed.player.removeAnalyticsListener(managed.analyticsListener)
                managed.analyticsListener.release()
                managed.player.release()
                throw CancellationException("Aborting prewarm for $mediaId: pool capacity reached ($maxPoolSize)")
            }

            players[mediaId] = managed
            poolUpdates.tryEmit(Unit)
            trimIfNeeded(excludeMediaId = if (urgent) mediaId else null)
            
            newDeferred.complete(managed)
            managed
        } catch (e: Throwable) {
            if (e !is CancellationException) {
                newDeferred.completeExceptionally(e)
            } else {
                newDeferred.cancel(e)
            }
            throw e
        } finally {
            inFlightCreations.remove(mediaId)
        }
    }

    private suspend fun ensureIdlePlayers() {
        while (idlePlayers.size < prewarmBudget) {
            val created = playerFactory.createIdle()
            
            // Immediately detach listener for pooling
            created.player.removeAnalyticsListener(created.analyticsListener)
            created.analyticsListener.release()
            
            created.player.stop()
            idlePlayers.addLast(created.player)
        }
    }

    private suspend fun bindIdlePlayer(
        player: ExoPlayer,
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        forceLegacy: Boolean,
        title: String?,
        artist: String?
    ): ManagedPlayer {
        val config = configurationFactory.create(mediaId, uri, mediaType, forceLegacy, title, artist)
        val listener = analyticsListenerProvider.get()
        
        player.addAnalyticsListener(listener)
        player.clearMediaItems()
        player.setMediaItem(config.mediaItem)
        player.playWhenReady = false
        player.prepare()
        
        return ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = player,
            analyticsListener = listener,
            reducer = PlaybackStateReducer(scope)
        )
    }

    private suspend fun createManagedPlayer(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        forceLegacy: Boolean,
        title: String?,
        artist: String?
    ): ManagedPlayer {
        val config = configurationFactory.create(mediaId, uri, mediaType, forceLegacy, title, artist)
        val created = playerFactory.create(config)

        val managed = ManagedPlayer(
            mediaId = mediaId,
            mediaType = mediaType,
            player = created.player,
            analyticsListener = created.analyticsListener,
            reducer = PlaybackStateReducer(scope)
        )

        managed.player.playWhenReady = false
        managed.player.prepare()
        return managed
    }

    fun forEachPlayer(block: (ExoPlayer, MediaType) -> Unit) {
        checkConfinement()
        players.values.forEach { block(it.player, it.mediaType) }
    }

    fun getMediaId(player: ExoPlayer): String? {
        checkConfinement()
        return players.entries.find { it.value.player == player }?.key
    }

    fun markAccessed(mediaId: String) {
        checkConfinement()
        players.get(mediaId)
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
                idlePlayers.addLast(managed.player)
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

        idlePlayers.forEach { player ->
            player.clearMediaItems()
            player.release()
        }
        idlePlayers.clear()
        poolUpdates.tryEmit(Unit)
    }

    fun notifyAppBackgrounded() {
        checkConfinement()
        players.values.forEach { it.analyticsListener.onAppBackgrounded() }
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

    // region Testing Hooks
    val debugPlayerCount: Int
        get() = players.size

    val debugMaxPoolSize: Int
        get() = maxPoolSize

    fun debugHasDuplicateInstances(): Boolean {
        val playerInstances = players.values.map { it.player }
        return playerInstances.size != playerInstances.distinct().size
    }
    // endregion
}
