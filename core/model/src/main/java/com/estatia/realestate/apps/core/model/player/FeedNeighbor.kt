package com.estatia.realestate.apps.core.model.player

import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

/**
 * Represents a neighboring video in a feed for prewarming/prefetching purposes.
 */
@DomainModel
data class FeedNeighbor(
    val mediaId: String,
    val uri: MediaReference,
    val matchScore: Float = 0.5f,
    val title: String? = null,
    val artist: String? = null
)
