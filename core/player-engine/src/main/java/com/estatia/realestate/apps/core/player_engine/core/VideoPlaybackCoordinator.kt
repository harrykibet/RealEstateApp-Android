package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoPlaybackCoordinator @Inject constructor(
    private val playerController: IPlayerManager,
    private val streamingPipeline: IStreamingPipeline
) {
    private var currentMediaId: String? = null
    private var playJob: Job? = null
    private var preloadJob: Job? = null

    companion object {
        private const val MAX_WARMED_MEDIA = 6
    }

    private val warmedMedia = LinkedHashSet<String>()

    fun observeState(): Flow<PlaybackStateReducer.State> = playerController.observeState()

    fun onPageVisible(
        scope: CoroutineScope,
        mediaId: String,
        uri: Uri,
        previous: FeedNeighborInfo?,
        next: FeedNeighborInfo?
    ) {
        if (currentMediaId == mediaId) return
        currentMediaId = mediaId

        playJob?.cancel()
        preloadJob?.cancel()

        playJob = scope.launch {
            playerController.play(mediaId, MediaType.VOD)
        }

        warmVisible(mediaId, uri)

        preloadJob = scope.launch {
            previous?.let {
                playerController.preload(it.mediaId, MediaType.VOD)
            }
            next?.let {
                playerController.preload(it.mediaId, MediaType.VOD)
                warmNext(it.mediaId, it.uri)
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
            streamingPipeline.warm(uri, WarmPriority.VISIBLE)
        }
    }

    private fun warmNext(mediaId: String, uri: Uri) {
        if (markWarmed(mediaId)) {
            streamingPipeline.warm(uri, WarmPriority.NEXT)
        }
    }

    suspend fun getPlayer(mediaId: String, mediaType: MediaType): Player {
        return playerController.getPlayer(mediaId, mediaType)
    }

    fun pause(scope: CoroutineScope) {
        scope.launch {
            playerController.pause()
        }
    }

    fun isMediaActive(mediaId: String): Boolean = currentMediaId == mediaId

    fun onBufferingStarted() = streamingPipeline.onBufferingStarted()
    fun onBufferingEnded() = streamingPipeline.onBufferingEnded()

    fun clear() {
        warmedMedia.clear()
        currentMediaId = null
    }
}

data class FeedNeighborInfo(
    val mediaId: String,
    val uri: Uri
)
