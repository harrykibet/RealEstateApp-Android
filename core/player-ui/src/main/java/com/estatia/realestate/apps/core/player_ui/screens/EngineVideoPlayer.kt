package com.estatia.realestate.apps.core.player_ui.screens

import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.property.MediaType

@Composable
fun EngineVideoPlayer(
    mediaId: String,
    mediaType: MediaType,
    getPlayer: suspend (String, MediaType) -> Player,
    onPause: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Stable player reference
    val playerState = remember(mediaId) {
        mutableStateOf<Player?>(null)
    }

    // Acquire player only when mediaId changes
    LaunchedEffect(mediaId, mediaType) {
        playerState.value = getPlayer(mediaId, mediaType)
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

    AndroidView(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
        factory = { surfaceView }
    )
}
