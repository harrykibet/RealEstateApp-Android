package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.core.RetryConfigs
import com.estatia.realestate.apps.core.architecture.annotations.Identity.DataSource

@DataSource
interface IRetryPolicy {


    suspend fun <T> execute(

        config: RetryConfig? = null,

        block:suspend()->T

    ):T
}
