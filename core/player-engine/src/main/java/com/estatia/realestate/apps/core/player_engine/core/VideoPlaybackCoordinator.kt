package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import android.os.SystemClock
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.player.FeedNeighbor
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import com.estatia.realestate.apps.core.common.system.PerformanceMonitor
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@ViewModelScoped
class VideoPlaybackCoordinator @Inject constructor(
    private val playerController: IPlayerManager,
    private val streamingPipeline: IStreamingPipeline,
    private val performanceMonitor: PerformanceMonitor,
    private val config: IPlayerTuningConfig
) {
    private var playJob: Job? = null
    private var preloadJob: Job? = null

    private var lastPageChangeTime: Long = 0
    private var consecutiveFastScrolls: Int = 0

    private val tuning get() = config.playerTuning

    private val warmedMedia = LinkedHashSet<String>()

    fun observeState(mediaId: String): Flow<PlaybackStateReducer.State> =
        playerController.observeState(mediaId)

    fun onPageVisible(
        scope: CoroutineScope,
        mediaId: String,
        uri: Uri,
        matchScore: Float,
        previous: List<FeedNeighbor>,
        next: List<FeedNeighbor>,
        title: String? = null,
        artist: String? = null
    ) {
        checkConfinement()
        
        if (playerController.activeMediaId == mediaId) return
        
        // Fling Heuristic: detect rapid flicking
        val now = SystemClock.elapsedRealtime()
        if (now - lastPageChangeTime < tuning.fastScrollThresholdMs) {
            consecutiveFastScrolls++
        } else {
            consecutiveFastScrolls = 0
        }
        lastPageChangeTime = now

        val isFlinging = consecutiveFastScrolls >= tuning.flingCountThreshold
        
        // 🏎️ Jank-Aware Interaction Blocking:
        // If the UI is struggling to keep up with frame deadlines, aggressively increase the debounce
        // to prioritize scroll smoothness over "magic" autoplay.
        val isJanking = performanceMonitor.isJanking.value
        
        val debounceTime = when {
            isJanking -> tuning.jankAwareDebounceMs
            isFlinging -> tuning.flingDebounceMs
            else -> tuning.dwellTimeDebounceMs
        }

        playJob?.cancel()
        preloadJob?.cancel()

        playJob = scope.launch {
            delay(debounceTime.milliseconds)
            warmVisible(mediaId, uri)
            playerController.play(mediaId, uri, MediaType.VOD, matchScore, title, artist)
        }

        // Only prewarm neighbors if not flinging to reduce list virtualization pressure
        if (!isFlinging) {
            preloadJob = scope.launch {
                delay(debounceTime.milliseconds) // Also debounce preloading

                // 1. Symmetric Warming: Warm previous neighbor (N=1)
                previous.firstOrNull()?.let {
                    if (it.matchScore > 0.5f) {
                        playerController.preload(it.mediaId, it.uri, MediaType.VOD, it.matchScore, it.title, it.artist)
                        warmPrevious(it.mediaId, it.uri)
                    }
                }

                // 2. Deep Warming: Warm next neighbors (N=2)
                next.getOrNull(0)?.let {
                    // 🏎️ Intelligent Prewarming: 
                    // High-match content gets deeper prefetch + hardware player prep
                    if (it.matchScore > 0.8f) {
                        playerController.preload(it.mediaId, it.uri, MediaType.VOD, it.matchScore, it.title, it.artist)
                    }
                    warmNext(it.mediaId, it.uri)
                }

                next.getOrNull(1)?.let {
                    if (it.matchScore > 0.4f) {
                        warmLow(it.mediaId, it.uri)
                    }
                }
            }
        }
    }

    private fun markWarmed(mediaId: String): Boolean {
        checkConfinement()
        val isNew = warmedMedia.add(mediaId)
        while (warmedMedia.size > tuning.maxWarmedMedia) {
            warmedMedia.remove(warmedMedia.first())
        }
        return isNew
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("VideoPlaybackCoordinator must only be accessed from the Main thread.")
        }
    }

    private fun warmVisible(mediaId: String, uri: Uri) {
        if (markWarmed(mediaId)) {
            streamingPipeline.warm(mediaId, uri, WarmPriority.VISIBLE)
        }
    }

    private fun warmNext(mediaId: String, uri: Uri) {
        if (markWarmed(mediaId)) {
            streamingPipeline.warm(mediaId, uri, WarmPriority.NEXT)
        }
    }

    private fun warmPrevious(mediaId: String, uri: Uri) {
        if (markWarmed(mediaId)) {
            streamingPipeline.warm(mediaId, uri, WarmPriority.PREVIOUS)
        }
    }

    private fun warmLow(mediaId: String, uri: Uri) {
        if (markWarmed(mediaId)) {
            streamingPipeline.warm(mediaId, uri, WarmPriority.LOW)
        }
    }

    suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType, matchScore: Float = 0.5f): Player {
        return playerController.getPlayer(mediaId, uri, mediaType, matchScore)
    }

    fun pause(scope: CoroutineScope) {
        scope.launch {
            playerController.pause()
        }
    }

    fun isMediaActive(mediaId: String): Boolean = playerController.isMediaActive(mediaId)

    fun retry(scope: CoroutineScope, mediaId: String, uri: Uri) {
        checkConfinement()
        playJob?.cancel()
        playJob = scope.launch {
            playerController.play(mediaId, uri, MediaType.VOD)
        }
    }

    fun onBufferingStarted() = streamingPipeline.onBufferingStarted()
    fun onBufferingEnded() = streamingPipeline.onBufferingEnded()

    fun clear() {
        checkConfinement()
        warmedMedia.clear()
        consecutiveFastScrolls = 0
    }
}
