package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.SystemClock
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import com.estatia.realestate.apps.core.common.system.PerformanceMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class VideoPlaybackCoordinator @Inject constructor(
    private val playerController: IPlayerManager,
    private val streamingPipeline: IStreamingPipeline,
    private val performanceMonitor: PerformanceMonitor
) {
    private var currentMediaId: String? = null
    private var playJob: Job? = null
    private var preloadJob: Job? = null

    private var lastPageChangeTime: Long = 0
    private var consecutiveFastScrolls: Int = 0

    companion object {
        private const val MAX_WARMED_MEDIA = 8
        private const val DWELL_TIME_DEBOUNCE_MS = 100L
        private const val JANK_AWARE_DEBOUNCE_MS = 400L
        private const val FLING_DEBOUNCE_MS = 250L
        private const val FAST_SCROLL_THRESHOLD_MS = 300L
        private const val FLING_COUNT_THRESHOLD = 3
    }

    private val warmedMedia = LinkedHashSet<String>()

    fun observeState(mediaId: String): Flow<PlaybackStateReducer.State> =
        playerController.observeState(mediaId)

    fun onPageVisible(
        scope: CoroutineScope,
        mediaId: String,
        uri: Uri,
        previous: List<FeedNeighborInfo>,
        next: List<FeedNeighborInfo>,
        forceLegacy: Boolean = false,
        title: String? = null,
        artist: String? = null
    ) {
        if (currentMediaId == mediaId) return
        currentMediaId = mediaId

        // Fling Heuristic: detect rapid flicking
        val now = SystemClock.elapsedRealtime()
        if (now - lastPageChangeTime < FAST_SCROLL_THRESHOLD_MS) {
            consecutiveFastScrolls++
        } else {
            consecutiveFastScrolls = 0
        }
        lastPageChangeTime = now

        val isFlinging = consecutiveFastScrolls >= FLING_COUNT_THRESHOLD
        
        // 🏎️ Jank-Aware Interaction Blocking:
        // If the UI is struggling to keep up with frame deadlines, aggressively increase the debounce
        // to prioritize scroll smoothness over "magic" autoplay.
        val isJanking = performanceMonitor.isJanking.value
        
        val debounceTime = when {
            isJanking -> JANK_AWARE_DEBOUNCE_MS
            isFlinging -> FLING_DEBOUNCE_MS
            else -> DWELL_TIME_DEBOUNCE_MS
        }

        playJob?.cancel()
        preloadJob?.cancel()

        playJob = scope.launch {
            delay(debounceTime.milliseconds)
            warmVisible(mediaId, uri)
            playerController.play(mediaId, uri, MediaType.VOD, forceLegacy, title, artist)
        }

        // Only prewarm neighbors if not flinging to reduce list virtualization pressure
        if (!isFlinging) {
            preloadJob = scope.launch {
                delay(debounceTime.milliseconds) // Also debounce preloading

                // 1. Symmetric Warming: Warm previous neighbor (N=1)
                previous.firstOrNull()?.let {
                    playerController.preload(it.mediaId, it.uri, MediaType.VOD, forceLegacy, it.title, it.artist)
                    warmPrevious(it.mediaId, it.uri)
                }

                // 2. Deep Warming: Warm next neighbors (N=2)
                next.getOrNull(0)?.let {
                    playerController.preload(it.mediaId, it.uri, MediaType.VOD, forceLegacy, it.title, it.artist)
                    warmNext(it.mediaId, it.uri)
                }

                next.getOrNull(1)?.let {
                    warmLow(it.mediaId, it.uri)
                }
            }
        }
    }

    private fun markWarmed(mediaId: String): Boolean {
        val isNew = warmedMedia.add(mediaId)
        while (warmedMedia.size > MAX_WARMED_MEDIA) {
            warmedMedia.remove(warmedMedia.first())
        }
        return isNew
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

    suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType): Player {
        return playerController.getPlayer(mediaId, uri, mediaType)
    }

    fun pause(scope: CoroutineScope) {
        scope.launch {
            playerController.pause()
        }
    }

    fun isMediaActive(mediaId: String): Boolean = currentMediaId == mediaId

    fun retry(scope: CoroutineScope, mediaId: String, uri: Uri, forceLegacy: Boolean = false) {
        playJob?.cancel()
        playJob = scope.launch {
            playerController.play(mediaId, uri, MediaType.VOD, forceLegacy)
        }
    }

    fun onBufferingStarted() = streamingPipeline.onBufferingStarted()
    fun onBufferingEnded() = streamingPipeline.onBufferingEnded()

    fun clear() {
        warmedMedia.clear()
        currentMediaId = null
        consecutiveFastScrolls = 0
    }
}

data class FeedNeighborInfo(
    val mediaId: String,
    val uri: Uri,
    val title: String? = null,
    val artist: String? = null
)
