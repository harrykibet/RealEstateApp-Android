package com.estatia.realestate.apps.feature.player.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.feature.player.state.PlayerUiState
import com.estatia.realestate.apps.core.ui.DevicePreviews

@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit,
) {

    val progress =
        uiState.positionMs.toFloat() / uiState.durationMs.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Slider(
            value = progress,
            onValueChange = onSeek,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (uiState.isPlaying)
                    Icons.Default.Pause
                else
                    Icons.Default.PlayArrow,
                contentDescription = null
            )
        }
    }
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    progress: Float, // 0f..1f
    onPlayPauseToggle: (Boolean) -> Unit,
    onSeekChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        Slider(
            value = progress,
            onValueChange = onSeekChanged,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        IconButton(
            onClick = {
                onPlayPauseToggle(!isPlaying)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)
@DevicePreviews
@Composable
fun PlayerControlsLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            PlayerControls(
                isPlaying = true,
                progress = 0.5f,
                onPlayPauseToggle = {},
                onSeekChanged = {}
            )
        }
    }
}