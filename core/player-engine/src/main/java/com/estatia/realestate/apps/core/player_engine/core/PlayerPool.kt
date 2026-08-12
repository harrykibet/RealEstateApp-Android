package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.IdentityHashMap
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
    private val configProvider: IConfigProvider,
    @param:EngineScope private val scope: CoroutineScope,
    poolSizingPolicy: IPlayerPoolSizingPolicy
) {
    private val inFlightCreations = mutableMapOf<String, InFlightRequest>()
    private val playerToIdMap = IdentityHashMap<Player, String>()

    private data class InFlightRequest(
        val deferred: CompletableDeferred<ManagedPlayer>,
        var isUrgent: Boolean
    )

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("PlayerPool must only be accessed from the player dispatcher thread (Main). " +
                    "Called from ${Thread.currentThread().name}, expected Main.")
        }
    }

    private val poolUpdates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private var maxPoolSize = poolSizingPolicy.calculateMaxPoolSize(environmentCoordinator.environment.value)
    private val players = LinkedHashMap<String, ManagedPlayer>(16, 0.75f, true)
    private val idlePlayers = ArrayDeque<ExoPlayer>()
    private val pinnedMediaIds = mutableSetOf<String>()
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
                players[mediaId]?.reducer?.state ?: flowOf(PlaybackStateReducer.State.Idle)
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
        return when (val result = prewarm(mediaId, uri, mediaType, forceLegacy, urgent = true, title = title, artist = artist)) {
            is PrewarmResult.Success -> result.managed
            is PrewarmResult.Failure -> throw result.throwable
            PrewarmResult.Rejected -> throw PoolCapacityExceededException("Urgent request rejected (should not happen)")
        }
    }

    suspend fun prewarm(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        forceLegacy: Boolean = false,
        urgent: Boolean = false,
        title: String? = null,
        artist: String? = null
    ): PrewarmResult {
        checkConfinement()
        
        // 1. Check if already active
        players[mediaId]?.let { return PrewarmResult.Success(it) }

        // 2. Check if creation is already in-flight for this specific ID
        val existing = inFlightCreations[mediaId]
        if (existing != null) {
            // 🏎️ Urgency Promotion: If a new urgent request arrives for a non-urgent in-flight task,
            // promote it so it becomes exempt from capacity rejection.
            if (urgent && !existing.isUrgent) {
                existing.isUrgent = true
            }
            return PrewarmResult.Success(existing.deferred.await())
        }

        // 3. Start a new creation task
        val newDeferred = CompletableDeferred<ManagedPlayer>()
        val request = InFlightRequest(newDeferred, urgent)
        inFlightCreations[mediaId] = request

        return try {
            // 🏎️ Proactive Refill: trigger background refill but don't wait for it here
            // unless the idle pool is completely empty.
            if (idlePlayers.isEmpty()) {
                ensureIdlePlayers()
            } else {
                scope.launch { ensureIdlePlayers() }
            }

            val managed = idlePlayers.removeFirstOrNull()?.let { player ->
                bindIdlePlayer(player, mediaId, uri, mediaType, forceLegacy, title, artist)
            } ?: createManagedPlayer(mediaId, uri, mediaType, forceLegacy, title, artist)

            // 🏎️ Late-bound Capacity Check:
            // Check the LATEST urgency state (may have been promoted while suspended).
            val currentUrgency = request.isUrgent
            if (!currentUrgency && players.size >= maxPoolSize && !players.containsKey(mediaId)) {
                managed.player.release()
                val error = PoolCapacityExceededException("Capacity reached ($maxPoolSize)")
                newDeferred.completeExceptionally(error)
                return PrewarmResult.Rejected
            }

            players[mediaId] = managed
            playerToIdMap[managed.player] = mediaId
            poolUpdates.tryEmit(Unit)
            trimIfNeeded(pinnedMediaIds)
            
            newDeferred.complete(managed)
            PrewarmResult.Success(managed)
        } catch (e: Throwable) {
            newDeferred.completeExceptionally(e)
            PrewarmResult.Failure(e)
        } finally {
            inFlightCreations.remove(mediaId)
        }
    }

    private fun ensureIdlePlayers() {
        while (idlePlayers.size < prewarmBudget) {
            val player = playerFactory.createIdle()
            player.stop()
            idlePlayers.addLast(player)
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
            reducer = PlaybackStateReducer(scope, configProvider.playerTuning.watchdogTimeoutMs)
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
            reducer = PlaybackStateReducer(scope, configProvider.playerTuning.watchdogTimeoutMs)
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
        return playerToIdMap[player]
    }

    fun release(mediaId: String) {
        checkConfinement()
        players.remove(mediaId)?.let { managed ->
            playerToIdMap.remove(managed.player)
            // 🛡️ Hygiene: Reset state to cancel in-flight watchdog jobs before recycling
            managed.reducer.dispatch(PlaybackStateReducer.Event.Reset)

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
        playerToIdMap.clear()

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

    fun updatePinnedIds(ids: Set<String>) {
        checkConfinement()
        pinnedMediaIds.clear()
        pinnedMediaIds.addAll(ids)
        trimIfNeeded(pinnedMediaIds)
    }

    fun updateMaxPoolSize(newSize: Int, pinnedIds: Set<String>) {
        checkConfinement()
        if (newSize == maxPoolSize) return
        if (newSize < maxPoolSize) {
            maxPoolSize = newSize
            updatePinnedIds(pinnedIds)
        } else {
            maxPoolSize = newSize
        }
    }

    fun trimIfNeeded(pinnedIds: Set<String>) {
        checkConfinement()
        if (players.size <= maxPoolSize) return

        val iterator = players.entries.iterator()
        while (iterator.hasNext() && players.size > maxPoolSize) {
            val entry = iterator.next()
            if (pinnedIds.contains(entry.key)) continue
            
            entry.value.player.removeAnalyticsListener(entry.value.analyticsListener)
            entry.value.analyticsListener.release()
            entry.value.player.clearMediaItems()
            entry.value.player.release()
            playerToIdMap.remove(entry.value.player)
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

    fun debugIsIdActive(mediaId: String): Boolean {
        return players.containsKey(mediaId)
    }
    // endregion
}
