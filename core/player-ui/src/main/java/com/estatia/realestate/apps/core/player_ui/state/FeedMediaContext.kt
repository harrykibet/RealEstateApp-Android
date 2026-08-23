package com.estatia.realestate.apps.core.player_ui.state

import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.player.FeedNeighbor

data class FeedMediaContext(
    val mediaId: String,
    val uri: MediaReference,
    val matchScore: Float = 0.5f,
    val title: String? = null,
    val artist: String? = null,
    val previous: List<FeedNeighbor> = emptyList(),
    val next: List<FeedNeighbor> = emptyList()
)
