package com.estatia.realestate.apps.feature.player.route

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.feature.player.core.ExoPlayerInstanceManager
import com.estatia.realestate.apps.feature.player.ui.screens.PlayerScreen
import com.estatia.realestate.apps.feature.player.viewModels.PlayerViewModel

@Composable
fun PlayerRoute(
    mediaId: String,
    exoplayer: ExoPlayerInstanceManager,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val playerView = remember {
        PlayerView(context).apply {
            useController = false
        }
    }

    AndroidView(
        factory = { playerView }, modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )

    // Attach player when media changes
    LaunchedEffect(mediaId) {
        exoplayer.attachPlayerToView(playerView, mediaId)
    }

    // Detach when media changes OR screen leaves
    DisposableEffect(mediaId, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoplayer.pause()
                Lifecycle.Event.ON_RESUME -> exoplayer.resume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoplayer.detachPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    PlayerScreen(
        uiState = uiState.value,
        onPlayPauseClick = viewModel::togglePlayback,
        onSeek = viewModel::seekToFraction
    )
}

