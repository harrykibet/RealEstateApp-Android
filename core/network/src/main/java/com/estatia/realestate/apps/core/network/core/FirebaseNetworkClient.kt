package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import javax.inject.Inject


class FirebaseNetworkClient @Inject constructor(
    private val networkStateProvider: INetworkStateProvider,
    private val retryPolicy: IRetryPolicy,
    private val exceptionMapper: IExceptionMapper,
    private val logger: LoggerInterface
) : INetworkClient {


    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): Result<T> {

        if (
            networkStateProvider.current()
            == NetworkState.NoInternet
        ) {
            return Result.Failure(
                NetworkException.NoInternet
            )
        }


        return try {

            Result.Success(
                retryPolicy.execute(
                    config,
                    apiCall
                )
            )

        } catch (
            throwable: Throwable
        ) {


            val exception =
                when (throwable) {

                    is AppException ->
                        throwable

                    else ->
                        exceptionMapper.map(
                            throwable
                        )
                }


            logger.e(
                message = "Remote operation failed",
                throwable = exception
            )


            Result.Failure(
                exception
            )
        }
    }
}