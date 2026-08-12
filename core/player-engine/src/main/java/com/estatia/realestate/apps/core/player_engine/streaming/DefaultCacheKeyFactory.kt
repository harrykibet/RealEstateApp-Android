package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import javax.inject.Inject

/**
 * Default implementation of [ICacheKeyFactory].
 */
internal class DefaultCacheKeyFactory @Inject constructor() : ICacheKeyFactory {

    override fun resolveStableKey(uri: Uri, providedId: String?, qualityHint: String?): String {
        // 🏗️ Strict ID Enforcement:
        // Every media item in Estatia MUST be identified by a stable content ID (mediaId/propertyId).
        // This prevents cache orphaning when delivery URLs (with signed tokens) rotate.
        if (providedId.isNullOrBlank()) {
            // In a production environment, we should never fall back to volatile URIs.
            throw IllegalArgumentException("Estatia media must have a stable identifier. Received null/blank ID for URI: $uri")
        }

        // 🌡️ Quality Awareness:
        // Append quality hint (if provided) to prevent cache collisions between different renditions
        // of the same media ID (e.g. progressive MP4 bitrate tiers).
        return if (qualityHint != null) {
            "$providedId:$qualityHint"
        } else {
            providedId
        }
    }
}
