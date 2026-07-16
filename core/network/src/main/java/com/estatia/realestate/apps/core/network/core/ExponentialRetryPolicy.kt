package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.RetryableException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


class ExponentialRetryPolicy @Inject constructor(
    private val exceptionMapper: IExceptionMapper
) : IRetryPolicy {


    override suspend fun <T> execute(
        config: RetryConfig,
        block: suspend () -> T
    ): T {


        var attempt = 0
        var delayMs = config.initialDelayMs
        var lastException: AppException? = null


        while(
            attempt < config.maxAttempts
        ) {


            try {

                return block()

            } catch(
                throwable: Throwable
            ) {


                val exception =
                    exceptionMapper.map(
                        throwable
                    )


                lastException = exception


                if(
                    !shouldRetry(
                        exception,
                        attempt,
                        config
                    )
                ) {
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
                                    config.multiplier
                            )
                        .toLong()
                        .coerceAtMost(
                            config.maxDelayMs
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