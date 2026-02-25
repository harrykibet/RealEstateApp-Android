package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.domain.interfaces.MediaType

@UnstableApi
interface IPlayerConfigurationFactory {
    fun create(
        uri: Uri,
        mediaType: MediaType
    ): PlayerConfiguration
}