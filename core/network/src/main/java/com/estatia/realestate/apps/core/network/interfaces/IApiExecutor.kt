package com.estatia.realestate.apps.core.network.interfaces

interface IApiExecutor {

    suspend fun <T> execute(
        maxRetries: Int = 3,
        apiCall: suspend () -> T
    ): Result<T>
}