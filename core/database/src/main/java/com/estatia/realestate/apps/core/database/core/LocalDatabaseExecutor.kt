package com.estatia.realestate.apps.core.database.core

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.IRoomExceptionMapper
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlinx.coroutines.CancellationException
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject

/**
 * Standard executor for local database operations.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Encapsulate try-catch blocks for Room/SQLite operations and translate to domain errors.
 * - Concurrency: Thread-safe; handles concurrent database access via Room's internal pool.
 * - Resilience: Surfaces domain-specific [InfrastructureException] via [exceptionMapper].
 * - Observability: Tracks operation latency and failure rates for database SLIs.
 */
internal class LocalDatabaseExecutor @Inject constructor(
    private val exceptionMapper: IRoomExceptionMapper,
    private val metricsTracker: IMetricsTracker,
    private val logger: ILogger
) : ILocalDatabaseExecutor {

    override suspend fun <T> execute(
        operation: suspend () -> T
    ): AppResult<T> {
        val startTime = System.currentTimeMillis()
        return try {
            val result = operation()
            
            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("database.operation.latency", duration.milliseconds)
            metricsTracker.incrementCounter("database.operation.success")
            
            AppResult.Success(result)
        } catch (cancellation: CancellationException) {
            // 🏎️ Fidelity: Rethrow cancellation to respect coroutine contracts.
            throw cancellation
        } catch (throwable: Throwable) {
            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("database.operation.latency", duration.milliseconds)
            metricsTracker.incrementCounter("database.operation.failure")

            val exception = exceptionMapper.map(throwable)

            exception.message?.let {
                logger.e(
                    tag = "LocalDatabaseExecutor",
                    message = it,
                    throwable = throwable
                )
            }

            AppResult.Error(exception)
        }
    }
}
