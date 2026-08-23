package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.network.core.NetworkState
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles automatic recovery of playback when network connection is restored.
 */
@UnstableApi
@Singleton
class NetworkRecoveryCoordinator @Inject constructor(
    private val networkStateProvider: INetworkStateProvider,
    private val pool: PlayerPool,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) {
    /**
     * Starts observing network state and triggers recovery for players in reconnecting state.
     */
    fun start() {
        checkConfinement()
        engineScope.launch(playerDispatcher) {
            networkStateProvider.observe().collect { state ->
                if (state is NetworkState.Connected) {
                    pool.forEachPlayer { player, _ ->
                        val mediaId = pool.getMediaId(player)
                        if (mediaId != null) {
                            val managed = pool.get(mediaId)
                            if (managed?.reducer?.state?.value is PlaybackStateReducer.State.Reconnecting) {
                                player.prepare()
                                managed.reducer.dispatch(PlaybackStateReducer.Event.NetworkRestored)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns true if the specified [PlaybackException] is caused by a network issue.
     */
    fun isNetworkError(error: PlaybackException): Boolean {
        return error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
    }

    /**
     * Returns the current network state.
     */
    fun getCurrentState(): NetworkState = networkStateProvider.current()

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("NetworkRecoveryCoordinator must only be accessed from the Main thread.")
        }
    }
}
