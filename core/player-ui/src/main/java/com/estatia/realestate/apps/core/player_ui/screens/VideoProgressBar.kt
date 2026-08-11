package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText

/**
 * An interactive progress bar for video playback.
 * Supports tap-to-seek and drag-to-scrub.
 */
@Composable
fun VideoProgressBar(
    progress: Float,
    bufferedProgress: Float,
    onSeekRequest: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(0f) }

    val displayProgress = if (isDragging) dragProgress else progress
    
    val height by animateDpAsState(
        targetValue = if (isDragging) 6.dp else 4.dp,
        label = "ProgressBarHeight"
    )

    Box(
        modifier = modifier
            .height(24.dp) // Large touch target
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        isDragging = false
                        onSeekRequest(dragProgress)
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { _, dragAmount ->
                        dragProgress = (dragProgress + dragAmount.x / size.width).coerceIn(0f, 1f)
                    }
                )
            }
            .pointerInput(Unit) {
                // Tap to seek
                detectTapGestures { offset ->
                    onSeekRequest((offset.x / size.width).coerceIn(0f, 1f))
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Track Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(Color.White.copy(alpha = 0.2f))
        )

        // Buffered Progress
        Box(
            modifier = Modifier
                .fillMaxWidth(bufferedProgress.coerceIn(0f, 1f))
                .height(height)
                .align(Alignment.CenterStart)
                .background(Color.White.copy(alpha = 0.4f))
        )

        // Active Progress
        Box(
            modifier = Modifier
                .fillMaxWidth(displayProgress.coerceIn(0f, 1f))
                .height(height)
                .align(Alignment.CenterStart)
                .background(if (isDragging) MaterialTheme.colorScheme.primary else Color.White)
        )

        // Knob (Handle)
        if (isDragging) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    // Note: We'd need a more accurate offset calculation for a real production UI
                    .fillMaxWidth(displayProgress)
                    .wrapContentWidth(Alignment.End)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
