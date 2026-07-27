package com.estatia.realestate.apps.core.security.core

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoExecutor @Inject constructor(
    private val translator: ISecurityExceptionTranslator,
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

            logger.e(
                tag = "CryptoExecutor",
                message = securityException.message ?: "Crypto operation failed",
                throwable = throwable
            )

            AppResult.Error(securityException)
        }
    }
}
