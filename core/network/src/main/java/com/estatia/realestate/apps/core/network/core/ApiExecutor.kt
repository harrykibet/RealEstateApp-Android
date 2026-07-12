package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.interfaces.IApiExecutor
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.network.utils.NetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiExecutor @Inject constructor(
    private val networkStateProvider: INetworkStateProvider,
    private val retryPolicy: IRetryPolicy,
    private val logger: LoggerInterface
) : IApiExecutor {


    override suspend fun <T> execute(
        maxRetries: Int,
        apiCall: suspend () -> T
    ): Result<T> {

        return withContext(Dispatchers.IO) {

            when (networkStateProvider.current()) {

                NetworkState.NoInternet -> {
                    Result.failure(
                        NetworkException.NoInternet
                    )
                }

                NetworkState.PoorConnection -> {

                    logger.w(
                        "Poor connection detected"
                    )

                    executeWithRetry(
                        maxRetries,
                        apiCall
                    )
                }

                NetworkState.Connected -> {

                    executeWithRetry(
                        maxRetries,
                        apiCall
                    )
                }
            }
        }
    }


    private suspend fun <T> executeWithRetry(
        maxRetries: Int,
        apiCall: suspend () -> T
    ): Result<T> {

        return try {

            Result.success(
                retryPolicy.execute(
                    maxRetries = maxRetries,
                    initialDelayMs = 1000,
                    block = apiCall
                )
            )

        } catch (exception: Exception) {

            logger.e(
                "API failed",
                exception
            )

            Result.failure(exception)
        }
    }
}