package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import javax.inject.Inject


class ProductionNetworkClient @Inject constructor(
    private val retryPolicy: IRetryPolicy,
    private val exceptionMapper: IExceptionMapper,
    private val logger: ILogger
) : INetworkClient {


    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): AppResult<T> {


        return try {

            AppResult.Success(
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


            AppResult.Error(
                exception
            )
        }
    }
}
