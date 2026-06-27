package com.estatia.realestate.apps.core.player_ui.screens

import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun EngineVideoPlayer(
    mediaId: String,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    viewModel: VideoPlaybackViewModel,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Stable player reference (NOT tied to recomposition key)
    val playerState = remember(mediaId) {
        mutableStateOf<Player?>(null)
    }

    // Acquire player only when mediaId changes
    LaunchedEffect(mediaId, mediaType) {
        playerState.value = viewModel.getPlayer(mediaId, mediaType)
    }

    val player = playerState.value

    // Stable surface lifecycle
    val surfaceView = remember(mediaId) {
        SurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Attach surface ONLY when player changes
    DisposableEffect(player) {
        player?.setVideoSurfaceView(surfaceView)

        onDispose {
            player?.clearVideoSurfaceView(surfaceView)
        }
    }

    // Lifecycle handling (FIXED: no global pause)
    DisposableEffect(lifecycleOwner, mediaId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // only pause if this is active media
                    if (viewModel.isActive(mediaId)) {
                        viewModel.pause()
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

    AndroidView(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
        factory = { surfaceView }
    )
}