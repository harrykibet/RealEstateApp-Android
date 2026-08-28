package com.estatia.realestate.apps.core.testing_network.chaos

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

/**
 * An adversarial implementation of [INetworkClient] that injects chaos based on multiple controllers.
 * 
 * 🏎️ OPERATIONAL FIDELITY:
 * Leverages the provided [retryPolicy] (ideally the production implementation) to drive
 * retries of scripted failure sequences, ensuring that the interaction between retry logic
 * and adversarial conditions is verified.
 */
class ChaosNetworkClient(
    private val networkChaos: NetworkChaosController,
    private val concurrencyChaos: ConcurrencyChaosController = ConcurrencyChaosController(),
    private val lifecycleChaos: LifecycleChaosController = LifecycleChaosController(),
    private val exceptionMapper: IExceptionMapper,
    private val retryPolicy: IRetryPolicy,
    private val onAttempt: () -> Unit = {}
) : INetworkClient {

    private val heldRequests = Channel<CompletableDeferred<Unit>>(Channel.UNLIMITED)

    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): AppResult<T> {
        return try {
            val data = retryPolicy.execute(config) {
                // 🏎️ Track attempt before any chaos can throw
                onAttempt()

                // 1. Inject before-request lifecycle/concurrency checks
                lifecycleChaos.checkChaos()
                concurrencyChaos.checkChaos("network_pre_execute")

                // 2. Retrieve scripted behavior
                val behavior = networkChaos.popNext()
                
                // 3. Handle Exception Injection immediately (Delay, Offline, Timeout)
                networkChaos.applyBehavior(behavior)
                
                // 4. SEMANTIC CHAOS: OutOfOrderResponse
                // Parks the current request until a subsequent non-held request completes.
                if (behavior == NetworkBehavior.OutOfOrderResponse) {
                    val releaseLatch = CompletableDeferred<Unit>()
                    heldRequests.send(releaseLatch)
                    releaseLatch.await()
                }

                // 5. Execute actual call
                var result = apiCall()

                // 6. SEMANTIC CHAOS: PartialResponse
                // Truncates data if the type is known (List, String).
                if (behavior == NetworkBehavior.PartialResponse) {
                    result = applyPartialTruncation(result)
                }

                // 7. Post-execution checks
                concurrencyChaos.checkChaos("network_post_execute")
                
                // 8. Release held requests (Simulates out-of-order delivery)
                if (behavior != NetworkBehavior.OutOfOrderResponse) {
                    heldRequests.tryReceive().getOrNull()?.complete(Unit)
                }

                result
            }
            AppResult.Success(data)
        } catch (e: CancellationException) {
            // 🏎️ Fidelity: Rethrow cancellation to respect coroutine contracts.
            throw e
        } catch (e: Exception) {
            // 5. Map to domain-friendly exceptions via production mapper
            AppResult.Error(exceptionMapper.map(e))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> applyPartialTruncation(data: T): T {
        return when (data) {
            is List<*> -> {
                if (data.size > 1) data.take(data.size / 2) as T else data
            }
            is String -> {
                if (data.length > 1) data.take(data.length / 2) as T else data
            }
            else -> data // Cannot safely truncate generic type
        }
    }
}
