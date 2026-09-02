package com.estatia.realestate.apps.core.testing.chaos.network

import com.estatia.realestate.apps.core.testing.chaos.server.ServerScenario
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicReference

/**
 * Controller for injecting network chaos into a [INetworkClient] or [IRemoteDataSource].
 */
class NetworkChaosController {

    private data class ScriptState(
        val behaviors: List<NetworkBehavior> = emptyList(),
        val index: Int = 0
    )

    private val scriptState = AtomicReference(ScriptState())
    private val serverScenario = AtomicReference<ServerScenario>(ServerScenario.ValidResponse)

    /**
     * Scripts a sequence of behaviors for subsequent requests.
     */
    fun script(vararg behaviors: NetworkBehavior) {
        scriptState.set(ScriptState(behaviors.toList(), 0))
    }

    /**
     * Sets the server-side scenario for subsequent requests.
     */
    fun setServerScenario(scenario: ServerScenario) {
        serverScenario.set(scenario)
    }

    /**
     * Retrieves the next behavior in the script without applying it.
     * Used by the client to handle semantic chaos.
     */
    fun popNext(): NetworkBehavior {
        while (true) {
            val current = scriptState.get()
            val behavior = if (current.index < current.behaviors.size) {
                current.behaviors[current.index]
            } else {
                NetworkBehavior.Success
            }
            
            // Only increment if we haven't reached the end
            val next = if (current.index < current.behaviors.size) {
                current.copy(index = current.index + 1)
            } else {
                current
            }

            if (scriptState.compareAndSet(current, next)) {
                return behavior
            }
        }
    }

    /**
     * Executes the next behavior in the script.
     * ⚠️ WARNING: This will throw for Exception Injection types but returns immediately
     * for Semantic types. Prefer using [popNext] in semantic-aware clients.
     */
    suspend fun executeNext() {
        val scenario = serverScenario.get()
        if (scenario != ServerScenario.ValidResponse) {
            applyServerScenario(scenario)
        }
        applyBehavior(popNext())
    }

    /**
     * Clears the current script and resets to Success.
     */
    fun reset() {
        scriptState.set(ScriptState())
        serverScenario.set(ServerScenario.ValidResponse)
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

    /**
     * Internal helper to apply the behavior (throw or delay).
     * Used by the client to drive the failure.
     */
    suspend fun applyBehavior(behavior: NetworkBehavior) {
        when (behavior) {
            NetworkBehavior.Success -> Unit
            NetworkBehavior.Offline -> throw IOException("No network connectivity (Chaos)")
            NetworkBehavior.Timeout -> throw SocketTimeoutException("Connection timed out (Chaos)")
            NetworkBehavior.ConnectionReset -> throw IOException("Connection reset by peer (Chaos)")
            NetworkBehavior.ConnectionRefused -> throw IOException("Connection refused (Chaos)")
            NetworkBehavior.DnsFailure -> throw IOException("DNS resolution failed (Chaos)")
            is NetworkBehavior.Delay -> delay(behavior.duration)
            is NetworkBehavior.HttpError -> throw HttpStatusException(behavior.statusCode)
            
            // --- Semantic Chaos (Handled by the Client after apiCall) ---
            NetworkBehavior.MalformedResponse,
            NetworkBehavior.EmptyResponse,
            NetworkBehavior.UnexpectedSchema,
            NetworkBehavior.OversizedResponse,
            NetworkBehavior.PartialResponse,
            NetworkBehavior.DuplicateResponse,
            NetworkBehavior.OutOfOrderResponse,
            NetworkBehavior.ServerSuccessClientTimeout -> Unit 
            
            is NetworkBehavior.InvalidBody -> throw HttpStatusException(400)
        }
    }
}

/**
 * Custom exception that carries HTTP status codes for chaos injection.
 * Allows the [IExceptionMapper] to correctly classify retryability based on status.
 */
class HttpStatusException(
    val statusCode: Int
) : IOException("HTTP $statusCode (Chaos)")
