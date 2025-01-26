package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.*
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
class MediaSessionManager(
    private val context: Context,
    private val player: MediaPlayer
) {
    private val mediaSession: MediaSession
    private val sessionProvider = DefaultMediaSessionProvider(context)

    init {
        // Initialize media session
        mediaSession = MediaSession.Builder(context, player.exoPlayer)
            .setSessionActivity(getPendingIntent())
            .setId("RealEstateAppMediaSession")
            .build()

        // Connect player to session
        sessionProvider.setSession(mediaSession)
        player.exoPlayer.setMediaSessionProvider(sessionProvider)
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, Class.forName("${context.packageName}.MainActivity"))
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun release() {
        mediaSession.release()
        sessionProvider.release()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.ConnectionResult> {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .addAllStandardCommands()
                .build()

            val playerCommands = Player.Commands.Builder()
                .addAll(
                    Player.COMMAND_PLAY_PAUSE,
                    Player.COMMAND_SEEK_TO_MEDIA_ITEM,
                    Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
                )
                .build()

            return Futures.immediateFuture(
                MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(sessionCommands)
                    .setAvailablePlayerCommands(playerCommands)
                    .build()
            )
        }
    }
}