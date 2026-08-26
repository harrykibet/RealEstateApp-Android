package com.estatia.realestate.apps.core.testing.chaos.network

import com.estatia.realestate.apps.core.testing.chaos.server.ServerScenario
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Controller for injecting network chaos into a [INetworkClient] or [IRemoteDataSource].
 */
class NetworkChaosController {

    private var script: List<NetworkBehavior> = emptyList()
    private var serverScenario: ServerScenario = ServerScenario.ValidResponse
    private var currentIndex = 0

    /**
     * Scripts a sequence of behaviors for subsequent requests.
     */
    fun script(vararg behaviors: NetworkBehavior) {
        script = behaviors.toList()
        currentIndex = 0
    }

    /**
     * Sets the server-side scenario for subsequent requests.
     */
    fun setServerScenario(scenario: ServerScenario) {
        serverScenario = scenario
    }

    /**
     * Executes the next behavior in the script, or Success if no script is active.
     */
    suspend fun executeNext() {
        if (serverScenario != ServerScenario.ValidResponse) {
            applyServerScenario(serverScenario)
        }

        val behavior = if (currentIndex < script.size) {
            script[currentIndex++]
        } else {
            NetworkBehavior.Success
        }

        applyBehavior(behavior)
    }

    private fun applyServerScenario(scenario: ServerScenario) {
        when (scenario) {
            ServerScenario.EmptyResponse -> throw IOException("Empty response (Chaos)")
            ServerScenario.MalformedJson -> throw IOException("Malformed JSON (Chaos)")
            ServerScenario.SchemaMismatch -> throw IOException("Schema mismatch (Chaos)")
            ServerScenario.PartialSuccess -> Unit // Handled by caller if needed
            ServerScenario.StaleVersion -> throw IOException("Stale version (Chaos)")
            ServerScenario.ValidationFailure -> throw IOException("Validation failure (Chaos)")
            ServerScenario.ValidResponse -> Unit
        }
    }

    private suspend fun applyBehavior(behavior: NetworkBehavior) {
        when (behavior) {
            NetworkBehavior.Success -> Unit
            NetworkBehavior.Offline -> throw IOException("No network connectivity (Chaos)")
            NetworkBehavior.Timeout -> throw SocketTimeoutException("Connection timed out (Chaos)")
            NetworkBehavior.ConnectionReset -> throw IOException("Connection reset by peer (Chaos)")
            NetworkBehavior.ConnectionRefused -> throw IOException("Connection refused (Chaos)")
            NetworkBehavior.DnsFailure -> throw IOException("DNS resolution failed (Chaos)")
            is NetworkBehavior.Delay -> delay(behavior.duration)
            is NetworkBehavior.HttpError -> throw IOException("HTTP ${behavior.statusCode} (Chaos)")
            NetworkBehavior.MalformedResponse -> throw IOException("Malformed response data (Chaos)")
            NetworkBehavior.EmptyResponse -> throw IOException("Empty response body (Chaos)")
            NetworkBehavior.PartialResponse -> throw IOException("Unexpected end of stream (Chaos)")
            NetworkBehavior.UnexpectedSchema -> throw IOException("Unexpected response schema (Chaos)")
            NetworkBehavior.OversizedResponse -> throw IOException("Response exceeds buffer size (Chaos)")
            NetworkBehavior.DuplicateResponse -> throw IOException("Duplicate network response (Chaos)")
            NetworkBehavior.OutOfOrderResponse -> throw IOException("Network response arrived out of order (Chaos)")
            NetworkBehavior.ServerSuccessClientTimeout -> {
                // 🧪 Specific case: Server processed the request but the client didn't wait long enough
                // to receive the 'success' acknowledgement.
                throw SocketTimeoutException("Read timeout after server side-effect (Chaos)")
            }
            is NetworkBehavior.InvalidBody -> throw IOException("HTTP 400 Bad Request (Chaos): ${behavior.payload}")
        }
    }
}
