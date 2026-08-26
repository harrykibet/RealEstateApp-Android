package com.estatia.realestate.apps.core.testing_network.chaos

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController

/**
 * An adversarial implementation of [INetworkClient] that injects chaos based on multiple controllers.
 */
class ChaosNetworkClient(
    private val networkChaos: NetworkChaosController,
    private val concurrencyChaos: ConcurrencyChaosController = ConcurrencyChaosController(),
    private val lifecycleChaos: LifecycleChaosController = LifecycleChaosController(),
    private val exceptionMapper: IExceptionMapper
) : INetworkClient {

    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): AppResult<T> {
        return try {
            // 1. Inject before-request lifecycle/concurrency checks
            lifecycleChaos.checkChaos()
            concurrencyChaos.checkChaos("network_pre_execute")

            // 2. Inject network-level chaos (e.g., Delay, Offline, Timeout)
            networkChaos.executeNext()
            
            // 3. Execute actual call
            val result = apiCall()

            // 4. Post-execution checks
            concurrencyChaos.checkChaos("network_post_execute")
            
            AppResult.Success(result)
        } catch (e: Exception) {
            // 5. Map to domain-friendly exceptions via production mapper
            AppResult.Error(exceptionMapper.map(e))
        }
    }
}
