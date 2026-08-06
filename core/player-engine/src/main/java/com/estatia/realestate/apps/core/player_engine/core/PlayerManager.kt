package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val sizingPolicy: IPlayerPoolSizingPolicy,
    private val dynamicBitrateController: DynamicBitrateController,
    private val streamingPipeline: IStreamingPipeline,
    @param:ApplicationContext private val context: Context,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) : IPlayerManager {

    private var activeMediaId: String? = null
    private val activeMediaIdFlow = MutableStateFlow<String?>(null)
    private val attachedPlayers = mutableSetOf<ExoPlayer>()
    private var activeFocusRequest: AudioFocusRequest? = null
    private val audioManager: AudioManager? by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }
    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        engineScope.launch(playerDispatcher) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> pauseCurrentPlayer()
                AudioManager.AUDIOFOCUS_GAIN -> resumeCurrentPlayer()
            }
        }
    }

    init {
        environmentCoordinator.start(engineScope)
        engineScope.launch(playerDispatcher) {
            environmentCoordinator.environment.collect { env ->
                val newSize = sizingPolicy.calculateMaxPoolSize()
                pool.updateMaxPoolSize(newSize, activeMediaId)
                pool.forEachPlayer { player, mediaType ->
                    dynamicBitrateController.apply(player, mediaType, env)
                }
            }
        }
    }

    override suspend fun play(mediaId: String, uri: Uri, mediaType: MediaType) =
        withContext(playerDispatcher) {
            val managed = pool.getOrCreate(mediaId, uri, mediaType)
            val environment = environmentCoordinator.environment.value

            if (activeMediaId != mediaId) {
                activeMediaId?.let { previous -> pool.get(previous)?.player?.pause() }
            }

            attachListenerIfNeeded(managed)
            managed.analyticsListener.markPlaybackStart()
            dynamicBitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
            managed.player.playWhenReady = true
            managed.player.prepare()
            managed.player.play()
            requestAudioFocus()
            activeMediaId = mediaId
            activeMediaIdFlow.value = mediaId
            streamingPipeline.warm(uri, WarmPriority.VISIBLE)
        }

    override suspend fun preload(mediaId: String, uri: Uri, mediaType: MediaType) =
        withContext(playerDispatcher) {
            val managed = pool.prewarm(mediaId, uri, mediaType)
            val environment = environmentCoordinator.environment.value
            dynamicBitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
            attachListenerIfNeeded(managed)
            managed
        }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            pauseCurrentPlayer()
            abandonAudioFocus()
        }
    }

    override suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType): Player =
        withContext(playerDispatcher) { pool.getOrCreate(mediaId, uri, mediaType).player }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeState(): StateFlow<PlaybackStateReducer.State> =
        activeMediaIdFlow
            .flatMapLatest { mediaId ->
                mediaId?.let { id -> pool.get(id)?.reducer?.state }
                    ?: MutableStateFlow(PlaybackStateReducer.State.Idle)
            }
            .flowOn(playerDispatcher)
            .stateIn(engineScope, SharingStarted.Eagerly, PlaybackStateReducer.State.Idle)

    override fun shutdown() {
        engineScope.launch(playerDispatcher) {
            pauseCurrentPlayer()
            abandonAudioFocus()
            pool.releaseAll()
            environmentCoordinator.stop()
        }
    }

    private fun requestAudioFocus(): Boolean {
        val audioManager = audioManager ?: return false

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
            .build()

        val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(attributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusListener)
            .build()

        activeFocusRequest = request
        return audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        activeFocusRequest?.let { request ->
            audioManager?.abandonAudioFocusRequest(request)
        }
        activeFocusRequest = null
    }

    private fun pauseCurrentPlayer() {
        activeMediaId?.let { pool.get(it)?.player?.pause() }
    }

    private fun resumeCurrentPlayer() {
        activeMediaId?.let { id ->
            pool.get(id)?.player?.let { player ->
                if (!player.isPlaying) {
                    player.play()
                }
            }
        }
    }

    private fun attachListenerIfNeeded(managed: PlayerPool.ManagedPlayer) {
        if (!attachedPlayers.add(managed.player)) return

        managed.player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> managed.reducer.dispatch(PlaybackStateReducer.Event.Reset)
                    Player.STATE_BUFFERING -> managed.reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
                    Player.STATE_READY -> managed.reducer.dispatch(PlaybackStateReducer.Event.BufferingCompleted)
                    Player.STATE_ENDED -> managed.reducer.dispatch(PlaybackStateReducer.Event.PlaybackEnded)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                managed.reducer.dispatch(
                    if (isPlaying) PlaybackStateReducer.Event.Play else PlaybackStateReducer.Event.Pause
                )
            }

            override fun onPlayerError(error: PlaybackException) {
                managed.reducer.dispatch(PlaybackStateReducer.Event.PlaybackError(error))
            }

            override fun onRenderedFirstFrame() {
                dynamicBitrateController.apply(
                    managed.player,
                    managed.mediaType,
                    environmentCoordinator.environment.value,
                    startupPhase = false
                )
            }
        })
    }
}
