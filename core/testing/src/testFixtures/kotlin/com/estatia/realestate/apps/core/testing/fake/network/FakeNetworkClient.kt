package com.estatia.realestate.apps.core.testing.fake.network

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient

/**
 * A deterministic fake implementation of [INetworkClient] that executes calls directly.
 */
class FakeNetworkClient : INetworkClient {

    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): AppResult<T> {
        return try {
            AppResult.Success(apiCall())
        } catch (e: Exception) {
            AppResult.Error(NetworkException.Unknown(e))
        }
    }
}
