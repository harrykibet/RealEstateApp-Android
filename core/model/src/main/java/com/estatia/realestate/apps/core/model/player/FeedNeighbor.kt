package com.estatia.realestate.apps.core.model.player

import com.estatia.realestate.apps.core.model.common.MediaReference

/**
 * Represents a neighboring video in a feed for prewarming/prefetching purposes.
 */
data class FeedNeighbor(
    val mediaId: String,
    val uri: MediaReference,
    val matchScore: Float = 0.5f,
    val title: String? = null,
    val artist: String? = null
)
