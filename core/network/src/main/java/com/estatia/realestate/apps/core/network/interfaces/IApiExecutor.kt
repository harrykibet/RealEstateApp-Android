package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.errors.Result

interface IApiExecutor {

    suspend fun <T> execute(
        maxRetries: Int = 3,
        apiCall: suspend () -> T
    ): Result<T>
}