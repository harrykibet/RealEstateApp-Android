package com.estatia.realestate.apps.core.player_ui.state

import android.net.Uri

data class FeedMediaContext(
    val mediaId: String,
    val uri: Uri,
    val previous: FeedNeighbor?,
    val next: FeedNeighbor?
)

data class FeedNeighbor(
    val mediaId: String,
    val uri: Uri
)