package com.estatia.realestate.apps.core.player_ui

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.ISharedPlayerController
import kotlinx.coroutines.launch

@Composable
fun EngineVideoPlayer(
    mediaId: String,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    controller: ISharedPlayerController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var player by remember { mutableStateOf<Player?>(null) }

    // Prepare and play
    LaunchedEffect(mediaId) {
        player = controller.getPlayer() // shared single player
        if (autoPlay) {
            controller.play(mediaId, mediaType)
        }
    }

    // Lifecycle handling
    DisposableEffect(lifecycleOwner, mediaId) {
        val observer = LifecycleEventObserver { _, event ->
            scope.launch {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> controller.play(mediaId, mediaType)
                    Lifecycle.Event.ON_PAUSE -> controller.pause()
                    Lifecycle.Event.ON_DESTROY -> controller.pause() // keep player alive if needed
                    else -> Unit
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // PlayerView rendering
    Box(modifier = modifier) {
        player?.let { exoPlayer ->
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        player = exoPlayer
                        useController = false
                    }
                },
                update = { view ->
                    if (view.player != exoPlayer) {
                        view.player = exoPlayer
                    }
                }
            )
        }
    }
}
