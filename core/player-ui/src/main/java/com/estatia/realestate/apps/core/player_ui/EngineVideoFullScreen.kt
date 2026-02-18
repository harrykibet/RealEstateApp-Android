package com.estatia.realestate.apps.core.player_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.ISharedPlayerController

@Composable
fun EngineVideoFullScreen(
    mediaId: String,
    mediaType: MediaType,
    controller: ISharedPlayerController,
    onExitFullScreen: () -> Unit
) {
    Dialog(onDismissRequest = onExitFullScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            // Reuse the same player
            EngineVideoPlayer(
                mediaId = mediaId,
                mediaType = mediaType,
                controller = controller,
                modifier = Modifier.fillMaxSize()
            )

            // Close button overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = onExitFullScreen) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Fullscreen",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
