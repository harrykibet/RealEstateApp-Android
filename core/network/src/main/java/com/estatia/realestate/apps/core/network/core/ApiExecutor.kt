package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.domain.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IApiExecutor
import com.estatia.realestate.apps.core.network.interfaces.INetworkErrorMapper
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.common.errors.Result
import javax.inject.Inject


class ApiExecutor @Inject constructor(
    private val networkStateProvider: INetworkStateProvider,
    private val retryPolicy: IRetryPolicy,
    private val errorMapper: INetworkErrorMapper,
    private val logger: LoggerInterface
) : IApiExecutor {


    override suspend fun <T> execute(
        config: RetryConfig,
        apiCall: suspend () -> T
    ): Result<T> {


        /*
         * Fast failure.
         *
         * Do not enter retry logic when
         * there is no network available.
         */
        if (
            networkStateProvider.current()
            == NetworkState.NoInternet
        ) {

            return Result.Failure(
                NetworkException.NoInternet
            )
        }



        return try {


            val response =
                retryPolicy.execute(
                    config = config,
                    block = apiCall
                )


            Result.Success(
                response
            )


        } catch(
            throwable: Throwable
        ) {


            val networkException =
                errorMapper.map(
                    throwable
                )


            logger.e(
                message = "API request failed",
                throwable = networkException
            )


            Result.Failure(
                networkException
            )
        }
    }
}