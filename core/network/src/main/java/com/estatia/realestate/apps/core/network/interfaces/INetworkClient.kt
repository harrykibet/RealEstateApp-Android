package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfig

/**
 * Interface for executing network requests with built-in retry logic and error handling.
 */
interface INetworkClient {

    /**
     * Executes a network call and maps results into [AppResult].
     * 
     * @param config Optional retry configuration to override defaults.
     * @param apiCall Suspend lambda containing the actual network operation.
     * @return [AppResult] containing either the success data or a mapped error.
     */
    suspend fun <T> execute(
        config: RetryConfig? = null,
        apiCall: suspend () -> T
    ): AppResult<T>

}
