import android.app.PendingIntent
import android.content.Context
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
    private val mediaPlayer: MediaPlayer
) {
    private val mediaSession: MediaSession by lazy { createMediaSession() }

    fun release() {
        mediaSession.run {
            player.release()
            release()
        }
    }

    // region Media Session Configuration
    private fun createMediaSession(): MediaSession = MediaSession.Builder(context, mediaPlayer.exoPlayer)
        .setSessionActivity(createSessionActivityIntent())
        .setId(SESSION_ID)
        .setCallback(MediaSessionCallback())
        .build()

    private fun createSessionActivityIntent(): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: throw IllegalStateException("No launch activity found")

        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_SESSION_ACTIVITY,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    // endregion

    // region Session Callback
    private inner class MediaSessionCallback : Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.ConnectionResult> {
            return Futures.immediateFuture(
                MediaSession.ConnectionResult.accept(
                    sessionCommands = buildSessionCommands(),
                    playerCommands = buildPlayerCommands()
                )
            )
        }

        private fun buildSessionCommands() =
            MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .addAllStandardCommands()
                .build()

        private fun buildPlayerCommands() =
            Player.Commands.Builder()
                .addAll(
                    Player.COMMAND_PLAY_PAUSE,
                    Player.COMMAND_SEEK_TO_MEDIA_ITEM,
                    Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM,
                    Player.COMMAND_SET_SPEED_AND_PITCH
                )
                .build()
    }
    // endregion

    companion object {
        private const val SESSION_ID = "RealEstateAppMediaSession"
        private const val REQUEST_CODE_SESSION_ACTIVITY = 1001
    }
}