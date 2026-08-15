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

        // 🛡️ Content Fingerprinting:
        // Incorporate a stable hash of the URI path to prevent "cache poisoning" between 
        // different renditions (e.g. different bitrates or codecs) that share the same mediaId.
        // We exclude query parameters to remain resilient to token rotation.
        val fingerprint = uri.path?.hashCode()?.toUInt()?.toString(16) ?: "default"

        return buildString {
            append(providedId)
            if (!qualityHint.isNullOrBlank()) {
                append(":")
                append(qualityHint)
            }
            append(":")
            append(fingerprint)
        }
    }
}
