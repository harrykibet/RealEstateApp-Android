package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.domain.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class ExponentialRetryPolicy @Inject constructor()
    : IRetryPolicy {


    override suspend fun <T> execute(
        config: RetryConfig,
        block: suspend () -> T
    ): T {


        var attempt = 0
        var delayMs = config.initialDelayMs


        while(true){

            try {

                return block()

            } catch(
                exception: NetworkException
            ){


                if(
                    !shouldRetry(
                        exception,
                        attempt,
                        config
                    )
                ){
                    throw exception
                }


                delay(addJitter(delayMs).milliseconds)


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
    }



    private fun shouldRetry(
        exception:NetworkException,
        attempt:Int,
        config:RetryConfig
    ):Boolean {


        if(attempt >= config.maxAttempts - 1)
            return false


        return when(exception){

            is NetworkException.Timeout,
            is NetworkException.ConnectionFailed,
            is NetworkException.ServerError ->
                true


            else ->
                false
        }
    }



    private fun addJitter(
        delay:Long
    ):Long {

        return delay +
                Random.nextLong(
                    delay / 2 + 1
                )
    }
}