package com.estatia.realestate.apps.core.player_ui.screens

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.AndroidExternalSurfaceZOrder
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.estatia.realestate.apps.core.model.property.MediaType
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * A reusable video player component that abstracts the surface lifecycle and player attachment.
 * Optimized for use within vertical feeds.
 * 
 * @param mediaId Unique identifier for the media.
 * @param uri Source URI for the video.
 * @param mediaType Type of media (LIVE/VOD).
 * @param getPlayer Suspend function to acquire a pooled player instance.
 * @param onPause Callback for when the video should pause (e.g., due to lifecycle events).
 * @param isActive Whether this player is currently the active (visible) one in a feed.
 */
@Composable
fun EngineVideoPlayer(
    mediaId: String,
    uri: Uri,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    matchScore: Float = 0.5f,
    getPlayer: suspend (String, Uri, MediaType, Float) -> Player,
    onPause: () -> Unit,
    isActive: Boolean,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    posterUri: Uri? = null,
    onLike: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val environmentState = com.estatia.realestate.apps.core.player_ui.core.LocalEnvironmentState.current
    val coroutineScope = rememberCoroutineScope()

    // Stable player reference
    val playerState = remember(mediaId) {
        mutableStateOf<Player?>(null)
    }

    // Acquire player only when mediaId/uri changes - reaction is now immediate
    LaunchedEffect(mediaId, uri, mediaType, matchScore, isActive) {
        if (isActive) {
            playerState.value = getPlayer(mediaId, uri, mediaType, matchScore)
        } else {
            playerState.value = null
        }
    }

    // 🏎️ Eviction Safety: Pin this mediaId while it's composed in the UI.
    // This prevents the pool from releasing the hardware resources for a visible video.
    val playerManager = com.estatia.realestate.apps.core.player_ui.core.LocalPlayerManager.current
    DisposableEffect(mediaId) {
        playerManager.notifyMediaBound(mediaId)
        onDispose {
            playerManager.notifyMediaUnbound(mediaId)
        }
    }

    val player = playerState.value

    // Playback control state - Synced with Player via Listener
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var showIndicator by remember { mutableStateOf(false) }
    var isHoldingPause by remember { mutableStateOf(false) }
    var wasPlayingBeforeHold by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var bufferedProgress by remember { mutableFloatStateOf(0f) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
            }
        }
        player?.addListener(listener)
        // Sync initial state
        isPlaying = player?.isPlaying ?: false
        isBuffering = player?.playbackState == Player.STATE_BUFFERING || player?.playbackState == Player.STATE_IDLE

        onDispose {
            player?.removeListener(listener)
        }
    }

    DisposableEffect(mediaId) {
        onDispose {
            onPause()
        }
    }

    // Progress polling - Environment aware
    LaunchedEffect(player, isActive, isPlaying, environmentState.isAppVisible, environmentState.isInteractive) {
        if (player != null && isActive && environmentState.isAppVisible && environmentState.isInteractive) {
            while (true) {
                val current = player.currentPosition.toDouble()
                val duration = player.duration.toDouble()
                if (duration > 0) {
                    progress = (current / duration).toFloat()
                }
                
                val buffered = player.bufferedPosition.toDouble()
                if (duration > 0) {
                    bufferedProgress = (buffered / duration).toFloat()
                }
                
                // ⏱️ Adaptive Polling: Reduce frequency when paused or throttled
                val pollInterval = when {
                    !isPlaying -> 1000.milliseconds
                    environmentState.shouldThrottlePerformance -> 500.milliseconds
                    else -> 200.milliseconds
                }
                
                delay(pollInterval)
            }
        }
    }

    // Sync volume with persistent mute preference
    LaunchedEffect(player, isMuted) {
        player?.volume = if (isMuted) 0f else 1f
    }

    // Lifecycle handling
    DisposableEffect(lifecycleOwner, mediaId, isActive) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (isActive) {
                        onPause()
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier) {
        Crossfade(
            targetState = isBuffering && posterUri != null,
            animationSpec = tween(durationMillis = 500),
            label = "VideoPosterCrossfade"
        ) { showPoster ->
            if (showPoster) {
                AsyncImage(
                    model = posterUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AndroidExternalSurface(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(player, isActive) {
                            detectTapGestures(
                                onDoubleTap = {
                                    onLike()
                                },
                                onLongPress = {
                                    // ✋ Hold-to-Pause: Long press pauses.
                                    // 🏎️ Snapshot state: only resume on release if it was actually playing.
                                    wasPlayingBeforeHold = player?.isPlaying == true
                                    isHoldingPause = true
                                    player?.pause()
                                },
                                onPress = {
                                    try {
                                        awaitRelease()
                                        // Resume on release ONLY if we were holding a pause AND it was playing before
                                        if (isHoldingPause && wasPlayingBeforeHold) {
                                            if (isActive && player != null) {
                                                player.play()
                                            }
                                        }
                                        isHoldingPause = false
                                        wasPlayingBeforeHold = false
                                    } catch (_: Exception) {
                                        isHoldingPause = false
                                        wasPlayingBeforeHold = false
                                    }
                                },
                                onTap = {
                                    if (player != null) {
                                        if (player.isPlaying) {
                                            player.pause()
                                        } else {
                                            player.play()
                                        }
                                        showIndicator = true
                                    }
                                }
                            )
                        },
                    zOrder = AndroidExternalSurfaceZOrder.Behind,
                    onInit = {
                        onSurface { surface, _, _ ->
                            player?.setVideoSurface(surface)
                            surface.onDestroyed {
                                player?.setVideoSurface(null)
                            }
                        }
                    }
                )
            }
        }

        // Play/Pause Indicator Overlay
        if (showIndicator && !isPlaying) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
            )

            // Auto-hide indicator
            LaunchedEffect(showIndicator) {
                delay(800.milliseconds)
                showIndicator = false
            }
        }

        // 🏗️ Advanced Video Progress Bar
        VideoProgressBar(
            progress = progress,
            bufferedProgress = bufferedProgress,
            onSeekRequest = { seekTo ->
                player?.let {
                    val targetMs = (seekTo * it.duration).toLong()
                    it.seekTo(targetMs)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // 🔊 Mute Toggle
        IconButton(
            onClick = onMuteToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
