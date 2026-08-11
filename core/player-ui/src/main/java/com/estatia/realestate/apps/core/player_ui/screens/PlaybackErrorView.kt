package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaOutlinedButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.player_ui.state.PlayerErrorType
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState

/**
 * A standardized error view for media playback failures.
 * Displays appropriate icons and messages based on the [PlayerErrorType].
 * 
 * @param errorState The specific error state containing the type and message.
 * @param onRetry Callback triggered when the user clicks the "Retry" button.
 */
@Composable
fun PlaybackErrorView(
    errorState: PlayerUiState.Error,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, message) = when (errorState.type) {
        PlayerErrorType.NETWORK -> 
            Icons.Default.CloudOff to "Network error. Please check your connection."
        PlayerErrorType.SERVER -> 
            Icons.Default.ErrorOutline to "Unable to load video from server."
        PlayerErrorType.DECODER -> 
            Icons.Default.ErrorOutline to "Video format not supported on this device."
        PlayerErrorType.NOT_FOUND -> 
            Icons.Default.ErrorOutline to "Video not found."
        PlayerErrorType.INVALID_URI -> 
            Icons.Default.ErrorOutline to "This video is currently unavailable."
        PlayerErrorType.UNKNOWN -> 
            Icons.Default.ErrorOutline to (errorState.message ?: "An unexpected error occurred.")
    }

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        EstatiaText(
            text = message,
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        EstatiaOutlinedButton(
            onClick = onRetry,
            text = { EstatiaText("Retry", color = Color.White) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        )
    }
}
