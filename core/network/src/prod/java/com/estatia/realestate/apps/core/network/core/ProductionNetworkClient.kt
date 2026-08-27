package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Standard implementation of [INetworkClient] for production environments.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: High-level network orchestration, including retry logic and domain error mapping.
 * - Concurrency: Stateless and thread-safe for concurrent invocations.
 * - Failure Modes: Automatically maps lower-level protocol exceptions to [AppException] via [exceptionMapper].
 * - Resilience: Enforces the provided [RetryConfig] via an injectable [IRetryPolicy].
 */
class ProductionNetworkClient @Inject constructor(
    private val retryPolicy: IRetryPolicy,
    private val exceptionMapper: IExceptionMapper,
    private val metricsTracker: IMetricsTracker,
    private val logger: ILogger
) : INetworkClient {


    override suspend fun <T> execute(
        config: RetryConfig?,
        apiCall: suspend () -> T
    ): AppResult<T> {

        val startTime = System.currentTimeMillis()

        return try {

            val data = retryPolicy.execute(
                config,
                apiCall
            )

            metricsTracker.trackDuration("network.client.latency", (System.currentTimeMillis() - startTime).milliseconds)
            metricsTracker.incrementCounter("network.client.success")

            AppResult.Success(data)

        } catch (cancellation: CancellationException) {
            // 🏎️ Fidelity: Rethrow cancellation to respect coroutine contracts.
            throw cancellation
        } catch (
            throwable: Throwable
        ) {
            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.client.latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.client.failure")


            val exception =
                when (throwable) {

                    is AppException ->
                        throwable

                    else ->
                        exceptionMapper.map(
                            throwable
                        )
                }


            logger.e(
                message = "Remote operation failed",
                throwable = exception
            )


            AppResult.Error(
                exception
            )
        }
    }
}
