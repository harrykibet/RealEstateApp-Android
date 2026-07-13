package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.network.core.RetryConfig

interface IApiExecutor {

    suspend fun <T> execute(
        config: RetryConfig,
        apiCall: suspend () -> T
    ): Result<T>

}