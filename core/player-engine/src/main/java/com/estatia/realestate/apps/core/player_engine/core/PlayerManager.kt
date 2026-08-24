package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Facade implementation of [IPlayerManager].
 * Composes specialized coordinators to handle specific playback responsibilities.
 */
@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val orchestrator: PlaybackOrchestrator,
    private val sessionCoordinator: MediaSessionCoordinator,
    private val networkRecovery: NetworkRecoveryCoordinator,
    private val audioFocusManager: AudioFocusManager,
    private val environmentManager: PlayerEnvironmentManager,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val pool: PlayerPool,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) : IPlayerManager {

    override val activeMediaId: String? get() = orchestrator.activeMediaId
    override val environment: StateFlow<EnvironmentState> = environmentCoordinator.environment

    private val composedMediaIds = mutableSetOf<String>()
    private var wasPlayingBeforePause: Boolean = false

    init {
        networkRecovery.start()
        
        audioFocusManager.setCallbacks(
            onLost = { orchestrator.pauseCurrentPlayer() },
            onGained = { orchestrator.resumeCurrentPlayer() }
        )

        environmentManager.start(
            onAppBackgrounded = {
                wasPlayingBeforePause = orchestrator.isCurrentlyPlaying()
                pause()
                pool.notifyAppBackgrounded()
            },
            onAppForegrounded = {
                if (wasPlayingBeforePause) {
                    orchestrator.resumeCurrentPlayer()
                }
            }
        )
    }

    override suspend fun play(
        mediaId: String,
        uri: MediaReference,
        mediaType: MediaType,
        matchScore: Float,
        title: String?,
        artist: String?
    ) {
        checkConfinement()
        orchestrator.play(mediaId, uri, mediaType, matchScore, title, artist)
        audioFocusManager.request()
        environmentManager.updateActiveMediaId(mediaId)
    }

    override suspend fun preload(
        mediaId: String,
        uri: MediaReference,
        mediaType: MediaType,
        matchScore: Float,
        title: String?,
        artist: String?
    ) {
        checkConfinement()
        orchestrator.preload(mediaId, uri, mediaType, matchScore, title, artist)
    }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            checkConfinement()
            orchestrator.pauseCurrentPlayer()
            audioFocusManager.abandon()
        }
    }

    override suspend fun getPlayer(mediaId: String, uri: MediaReference, mediaType: MediaType, matchScore: Float): Player =
        withContext(playerDispatcher) {
            checkConfinement()
            pool.getOrCreate(mediaId, uri, mediaType, matchScore).player
        }

    override fun observeState(mediaId: String): Flow<PlaybackStateReducer.State> =
        pool.observeMediaState(mediaId)

    override fun shutdown() {
        engineScope.launch(playerDispatcher) {
            checkConfinement()
            orchestrator.pauseCurrentPlayer()
            audioFocusManager.abandon()
            audioFocusManager.cleanup()
            sessionCoordinator.release()
            pool.releaseAll()
            orchestrator.clearAttachedPlayers()
            environmentManager.stop()
        }
    }

    override fun isPlaying(): Boolean {
        checkConfinement()
        return orchestrator.isCurrentlyPlaying()
    }

    override fun isMediaActive(mediaId: String): Boolean {
        checkConfinement()
        return activeMediaId == mediaId
    }

    override fun notifyMediaBound(mediaId: String) {
        checkConfinement()
        composedMediaIds.add(mediaId)
        environmentManager.updatePinnedIds(composedMediaIds)
        pool.updatePinnedIds(composedMediaIds)
    }

    override fun notifyMediaUnbound(mediaId: String) {
        checkConfinement()
        composedMediaIds.remove(mediaId)
        environmentManager.updatePinnedIds(composedMediaIds)
        pool.updatePinnedIds(composedMediaIds)
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("PlayerManager must only be accessed from the Main thread.")
        }
    }

    // region Testing Hooks
    val debugActiveMediaId: String?
        get() = activeMediaId

    val debugAttachedPlayersCount: Int
        get() = orchestrator.debugAttachedPlayersCount
    // endregion
}
