package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.core.RetryConfig

interface IRetryPolicy {


    suspend fun <T> execute(

        config: RetryConfig,

        block:suspend()->T

    ):T
}