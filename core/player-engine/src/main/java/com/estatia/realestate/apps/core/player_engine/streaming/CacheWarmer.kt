package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import com.estatia.realestate.apps.core.architecture.annotations.Data.PlayerState

enum class WarmPriority {
    VISIBLE,
    NEXT,
    PREVIOUS,
    LOW
}

@PlayerState
data class WarmRequest(
    val uri: Uri,
    val priority: WarmPriority,
    val mediaId: String,
    val qualityHint: String? = null
)
