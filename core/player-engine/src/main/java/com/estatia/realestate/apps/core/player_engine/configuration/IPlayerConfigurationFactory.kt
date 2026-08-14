package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType

/**
 * Factory for creating [PlayerConfiguration] instances based on media URI and type.
 */
@UnstableApi
interface IPlayerConfigurationFactory {
    /**
     * Creates a configuration for the specified media.
     * Handles CDN resolution and component orchestration.
     * 
     * @param mediaId Unique identifier for the media.
     * @param uri Source URI for the media.
     * @param mediaType Type of media (LIVE/VOD).
     * @return A fully populated [PlayerConfiguration].
     */
    suspend fun create(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        matchScore: Float = 0.5f,
        forceLegacyCodec: Boolean = false,
        title: String? = null,
        artist: String? = null
    ): PlayerConfiguration
}
