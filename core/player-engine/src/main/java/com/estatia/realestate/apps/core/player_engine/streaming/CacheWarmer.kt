package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri

enum class WarmPriority {
    VISIBLE,
    NEXT,
    PREVIOUS,
    LOW
}

data class WarmRequest(
    val uri: Uri,
    val priority: WarmPriority,
    val mediaId: String,
    val qualityHint: String? = null
)
