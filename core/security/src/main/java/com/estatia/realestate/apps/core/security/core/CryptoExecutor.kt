package com.estatia.realestate.apps.core.security.core

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Standard executor for cryptographic operations.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Encapsulate try-catch blocks for Java Crypto operations and translate to domain errors.
 * - Concurrency: Stateless and thread-safe.
 * - Observability: Records 'security.crypto.failure' metrics upon translation.
 * - Resilience: Provides consistent fallback [defaultException] when translation is ambiguous.
 */
@Singleton
class CryptoExecutor @Inject constructor(
    private val translator: ISecurityExceptionTranslator,
    private val metricsTracker: IMetricsTracker,
    private val logger: ILogger
) : ICryptoExecutor {

    override suspend fun <T> execute(
        defaultException: SecurityException,
        operation: suspend () -> T
    ): AppResult<T> {
        return try {
            AppResult.Success(operation())
        } catch (throwable: Throwable) {
            val securityException = translator.translate(throwable, defaultException)

            metricsTracker.incrementCounter("security.crypto.failure")
            
            logger.e(
                tag = "CryptoExecutor",
                message = securityException.message ?: "Crypto operation failed",
                throwable = throwable
            )

            AppResult.Error(securityException)
        }
    }
}
