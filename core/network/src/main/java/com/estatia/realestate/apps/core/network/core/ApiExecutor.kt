package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiExecutor @Inject constructor(
    private val networkStateProvider: INetworkStateProvider,
    private val retryPolicy: IRetryPolicy,
    private val logger: LoggerInterface
) {

    suspend fun <T> execute(
        maxRetries: Int = 3,
        apiCall: suspend () -> T,
        onFailure: (Exception) -> Unit
    ): T? {

        return withContext(Dispatchers.IO) {

            when (networkStateProvider.current()) {

                NetworkState.NoInternet -> {
                    onFailure(Exception("NO_INTERNET"))
                    return@withContext null
                }

                NetworkState.PoorConnection -> {
                    logger.w("Poor connection detected")
                }

                NetworkState.Connected -> Unit
            }

            try {
                retryPolicy.execute(
                    maxRetries = maxRetries,
                    initialDelayMs = 1000,
                    block = apiCall
                )
            } catch (e: Exception) {
                logger.e("API failed", e)
                onFailure(e)
                null
            }
        }
    }
}