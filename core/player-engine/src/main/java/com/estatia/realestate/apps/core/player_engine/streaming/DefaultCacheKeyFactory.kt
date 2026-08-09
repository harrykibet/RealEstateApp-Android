package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import javax.inject.Inject

/**
 * Default implementation of [ICacheKeyFactory].
 */
internal class DefaultCacheKeyFactory @Inject constructor() : ICacheKeyFactory {

    override fun resolveStableKey(uri: Uri, providedId: String?): String {
        // 1. If a high-level ID (like propertyId) is provided, it's the most stable key.
        if (!providedId.isNullOrBlank()) {
            return providedId
        }

        // 2. Otherwise, derive from URI by stripping volatile components like query params (auth tokens).
        val builder = uri.buildUpon().clearQuery()
        
        // If it's a file URI, we can just use the path.
        if (uri.scheme == "file") {
            return uri.path ?: uri.toString()
        }

        return builder.build().toString()
    }
}
