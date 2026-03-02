package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri

enum class PrefetchPriority {
    VISIBLE,
    NEXT
}

data class PrefetchRequest(
    val uri: Uri,
    val priority: PrefetchPriority
)