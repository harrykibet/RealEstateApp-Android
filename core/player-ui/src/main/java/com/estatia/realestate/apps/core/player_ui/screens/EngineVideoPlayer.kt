package com.estatia.realestate.apps.core.player_ui.screens

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
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun EngineVideoPlayer(
    mediaId: String,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    viewModel: VideoPlaybackViewModel,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var player by remember(mediaId) { mutableStateOf<Player?>(null) }

    // Obtain the correct player for this mediaId
    LaunchedEffect(mediaId) {
        player = viewModel.getPlayer(mediaId)

        if (autoPlay) {
            viewModel.play(mediaId, mediaType)
        }
    }

    // Lifecycle handling
    DisposableEffect(lifecycleOwner, mediaId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.play(mediaId, mediaType)
                Lifecycle.Event.ON_PAUSE -> viewModel.pause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Render PlayerView
    player?.let { attachedPlayer ->
        AndroidView(
            modifier = modifier.then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
            factory = {
                PlayerView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    player = attachedPlayer
                    useController = false
                }
            },
            update = { view ->
                if (view.player != attachedPlayer) {
                    view.player = attachedPlayer
                }
            }
        )
    }
}
