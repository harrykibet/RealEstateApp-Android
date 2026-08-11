package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class MediaSessionProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : IMediaSessionProvider {
    override fun create(player: Player): MediaSession {
        return MediaSession.Builder(context, player).build()
    }
}
