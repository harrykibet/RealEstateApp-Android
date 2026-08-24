package com.estatia.realestate.apps.core.testing.chaos.network

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper

/**
 * An adversarial implementation of [INetworkClient] that injects chaos based on a [NetworkChaosController].
 */
class ChaosNetworkClient(
    private val chaos: NetworkChaosController,
    private val exceptionMapper: IExceptionMapper
) : INetworkClient {

    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): AppResult<T> {
        return try {
            // 1. Inject before-request chaos (e.g., Delay, Offline, Timeout)
            chaos.executeNext()
            
            // 2. Execute actual call
            AppResult.Success(apiCall())
        } catch (e: Exception) {
            // 3. Map to domain-friendly exceptions via production mapper
            AppResult.Error(exceptionMapper.map(e))
        }
    }
}
