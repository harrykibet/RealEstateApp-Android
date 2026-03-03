package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri

enum class WarmPriority {
    VISIBLE,
    NEXT
}

data class WarmRequest(
    val uri: Uri,
    val priority: WarmPriority
)