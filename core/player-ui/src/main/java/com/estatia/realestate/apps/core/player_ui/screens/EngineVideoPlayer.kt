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

    var player by remember(mediaId) { mutableStateOf<Player?>(null) }

    // Acquire player instance
    LaunchedEffect(mediaId) {
        player = viewModel.getPlayer(mediaId, mediaType)
    }

    // Remember SurfaceView so it's not recreated on recomposition
    val surfaceView = remember {
        SurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Attach / detach surface from player
    DisposableEffect(player) {
        val currentPlayer = player

        currentPlayer?.setVideoSurfaceView(surfaceView)

        onDispose {
            currentPlayer?.clearVideoSurfaceView(surfaceView)
        }
    }

    // Lifecycle handling (pause only)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.pause()
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