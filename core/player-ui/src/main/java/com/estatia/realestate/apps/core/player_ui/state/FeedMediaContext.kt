package com.estatia.realestate.apps.core.player_ui.state

import android.net.Uri

data class FeedMediaContext(
    val mediaId: String,
    val uri: Uri,
    val previous: List<FeedNeighbor> = emptyList(),
    val next: List<FeedNeighbor> = emptyList()
)

data class FeedNeighbor(
    val mediaId: String,
    val uri: Uri
)
