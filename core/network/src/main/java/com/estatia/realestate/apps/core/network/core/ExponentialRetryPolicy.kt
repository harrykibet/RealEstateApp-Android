package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ExponentialRetryPolicy @Inject constructor() : IRetryPolicy {

    override suspend fun <T> execute(
        maxRetries: Int,
        initialDelayMs: Long,
        block: suspend () -> T
    ): T {

        var lastError: Exception? = null
        var delayMs = initialDelayMs

        repeat(maxRetries) { attempt ->

            try {
                return block()
            } catch (e: Exception) {
                lastError = e
                if (attempt < maxRetries - 1) {
                    kotlinx.coroutines.delay(delayMs.milliseconds)
                    delayMs *= 2
                }
            }
        }

        throw lastError ?: IllegalStateException("Retry failed without exception")
    }
}