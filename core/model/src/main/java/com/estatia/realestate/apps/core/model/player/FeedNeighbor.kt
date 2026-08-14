package com.estatia.realestate.apps.core.model.player

import android.net.Uri

/**
 * Represents a neighboring video in a feed for prewarming/prefetching purposes.
 */
data class FeedNeighbor(
    val mediaId: String,
    val uri: Uri,
    val matchScore: Float = 0.5f,
    val title: String? = null,
    val artist: String? = null
)
