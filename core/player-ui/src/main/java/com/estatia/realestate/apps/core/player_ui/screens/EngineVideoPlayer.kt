package com.estatia.realestate.apps.core.player_ui.screens

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_ui.core.SurfacePool
import kotlinx.coroutines.delay

@Composable
fun EngineVideoPlayer(
    mediaId: String,
    uri: Uri,
    mediaType: MediaType,
    getPlayer: suspend (String, Uri, MediaType) -> Player,
    onPause: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Stable player reference
    val playerState = remember(mediaId) {
        mutableStateOf<Player?>(null)
    }

    // Acquire player only when mediaId/uri changes
    LaunchedEffect(mediaId, uri, mediaType) {
        playerState.value = getPlayer(mediaId, uri, mediaType)
    }

    val player = playerState.value

    // Stable surface lifecycle via Pool
    val surfaceView = remember(mediaId) {
        SurfacePool.acquire(context)
    }

    DisposableEffect(mediaId) {
        onDispose {
            SurfacePool.release(surfaceView)
        }
    }

    // Playback control state
    var isPlaying by remember { mutableStateOf(true) }
    var showIndicator by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Sync isPlaying with player state and isActive
    LaunchedEffect(player, isActive) {
        if (player != null && isActive) {
            isPlaying = player.playWhenReady
        }
    }

    // Progress polling
    LaunchedEffect(player, isActive, isPlaying) {
        if (player != null && isActive && isPlaying) {
            while (true) {
                val current = player.currentPosition.toDouble()
                val duration = player.duration.toDouble()
                if (duration > 0) {
                    progress = (current / duration).toFloat()
                }
                delay(200)
            }
        }
    }

    // Attach surface ONLY when player changes
    DisposableEffect(player, surfaceView) {
        player?.setVideoSurfaceView(surfaceView)

        onDispose {
            player?.clearVideoSurfaceView(surfaceView)
        }
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
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (player != null) {
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.play()
                            isPlaying = true
                        }
                        showIndicator = true
                    }
                },
            factory = { surfaceView }
        )

        // Play/Pause Indicator Overlay
        val indicatorAlpha by animateFloatAsState(
            targetValue = if (showIndicator && !isPlaying) 1f else 0f,
            label = "IndicatorAlpha"
        )

        if (showIndicator && !isPlaying) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .alpha(indicatorAlpha)
            )

            // Auto-hide indicator if it was just a transient tap
            LaunchedEffect(showIndicator) {
                delay(800)
                showIndicator = false
            }
        }

        // Progress Bar at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(2.dp)
                    .background(Color.White)
            )
        }
    }
}
