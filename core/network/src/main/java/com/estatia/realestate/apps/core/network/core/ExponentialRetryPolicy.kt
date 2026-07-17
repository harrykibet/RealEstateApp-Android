package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.RetryableException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


class ExponentialRetryPolicy @Inject constructor(
    private val exceptionMapper: IExceptionMapper
) : IRetryPolicy {


    override suspend fun <T> execute(
        config: RetryConfig?,
        block: suspend () -> T
    ): T {


        val retryConfig =
            config ?: RetryConfigs.NO_RETRY


        var attempt = 0
        var delayMs = retryConfig.initialDelayMs
        var lastException: AppException? = null


        while(
            attempt < retryConfig.maxAttempts
        ) {

            try {

                return block()

            } catch(
                throwable: Throwable
            ) {

                if (throwable is CancellationException) {
                    throw throwable
                }


                val exception =
                    when(throwable){

                        is AppException ->
                            throwable

                        else ->
                            exceptionMapper.map(
                                throwable
                            )
                    }


                lastException = exception


                if(
                    !shouldRetry(
                        exception,
                        attempt,
                        retryConfig
                    )
                ){
                    throw exception
                }


                delay(
                    addJitter(delayMs)
                        .milliseconds
                )


                attempt++


                delayMs =
                    (
                            delayMs *
                                    retryConfig.multiplier
                            )
                        .toLong()
                        .coerceAtMost(
                            retryConfig.maxDelayMs
                        )
            }
        }


        throw lastException
            ?: IllegalStateException(
                "Retry failed without exception"
            )
    }



    private fun shouldRetry(
        exception: AppException,
        attempt: Int,
        config: RetryConfig
    ): Boolean {


        if(
            attempt >= config.maxAttempts - 1
        ) {
            return false
        }


        return exception is RetryableException
    }



    private fun addJitter(
        delay: Long
    ): Long {

        return delay +
                Random.nextLong(
                    delay / 2 + 1
                )
    }
}