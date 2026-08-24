package com.estatia.realestate.apps.core.testing.chaos.network

import kotlinx.coroutines.delay
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.time.Duration

/**
 * Controller for injecting network chaos into a [INetworkClient] or [IRemoteDataSource].
 */
class NetworkChaosController {

    private var script: List<NetworkBehavior> = emptyList()
    private var currentIndex = 0

    /**
     * Scripts a sequence of behaviors for subsequent requests.
     */
    fun script(vararg behaviors: NetworkBehavior) {
        script = behaviors.toList()
        currentIndex = 0
    }

    /**
     * Executes the next behavior in the script, or Success if no script is active.
     */
    suspend fun executeNext() {
        val behavior = if (currentIndex < script.size) {
            script[currentIndex++]
        } else {
            NetworkBehavior.Success
        }

        applyBehavior(behavior)
    }

    private suspend fun applyBehavior(behavior: NetworkBehavior) {
        when (behavior) {
            NetworkBehavior.Success -> Unit
            NetworkBehavior.Offline -> throw IOException("No network connectivity (Chaos)")
            NetworkBehavior.Timeout -> throw SocketTimeoutException("Connection timed out (Chaos)")
            NetworkBehavior.ConnectionReset -> throw IOException("Connection reset by peer (Chaos)")
            is NetworkBehavior.Delay -> delay(behavior.duration)
            is NetworkBehavior.HttpError -> {
                // This would usually be handled by the response mapping layer
                // but we can throw a generic exception here that our mappers recognize
                throw IOException("HTTP ${behavior.statusCode} (Chaos)")
            }
            is NetworkBehavior.InvalidBody -> Unit // Handled by actual response injection
        }
    }
}
