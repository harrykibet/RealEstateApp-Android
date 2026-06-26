package com.estatia.realestate.apps.core.network.interfaces

interface IRetryPolicy {
    suspend fun <T> execute(
        maxRetries: Int,
        initialDelayMs: Long,
        block: suspend () -> T
    ): T
}