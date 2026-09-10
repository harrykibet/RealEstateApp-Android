package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Helper

// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Singleton
@Helper
class MediaSessionProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : IMediaSessionProvider {
    override fun create(player: Player): MediaSession {
        return MediaSession.Builder(context, player).build()
    }
}
