package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri

/**
 * Interface for deriving stable cache keys and media IDs from media URIs.
 * Ensures that logical content is correctly identified even if delivery URLs change (e.g., token rotation).
 */
interface ICacheKeyFactory {
    /**
     * Resolves a stable key for the given [uri].
     * 
     * @param uri The media source URI.
     * @param providedId An optional identifier provided by the caller (e.g., property ID).
     * @return A stable string key to be used for caching and media identification.
     */
    fun resolveStableKey(uri: Uri, providedId: String? = null): String
}
