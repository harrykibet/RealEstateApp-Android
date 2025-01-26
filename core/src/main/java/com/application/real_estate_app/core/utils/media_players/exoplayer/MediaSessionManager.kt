import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.Callback
import com.application.real_estate_app.core.utils.media_players.exoplayer.MediaPlayer
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
class MediaSessionManager(
    private val context: Context,
    private val player: MediaPlayer
) {
    private val mediaSession: MediaSession

    init {
        mediaSession = MediaSession.Builder(context, player.exoPlayer)
            .setSessionActivity(getPendingIntent())
            .setId("RealEstateAppMediaSession")
            .setCallback(MediaSessionCallback()) // Add callback directly here
            .build()
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
    }

    private inner class MediaSessionCallback : Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.ConnectionResult> {
            return Futures.immediateFuture(
                MediaSession.ConnectionResult.accept(
                    sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                        .addAllStandardCommands()
                        .build(),
                    playerCommands = Player.Commands.Builder()
                        .addAll(
                            Player.COMMAND_PLAY_PAUSE,
                            Player.COMMAND_SEEK_TO_MEDIA_ITEM,
                            Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
                        )
                        .build()
                )
            )
        }
    }
}