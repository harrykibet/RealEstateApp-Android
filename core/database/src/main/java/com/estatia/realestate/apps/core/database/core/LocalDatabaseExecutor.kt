package com.estatia.realestate.apps.core.database.core

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.IRoomExceptionMapper
import javax.inject.Inject

class LocalDatabaseExecutor @Inject constructor(
    private val exceptionMapper: IRoomExceptionMapper,
    private val logger: ILogger
) : ILocalDatabaseExecutor {

    override suspend fun <T> execute(
        operation: suspend () -> T
    ): AppResult<T> {
        return try {
            AppResult.Success(operation())
        } catch (throwable: Throwable) {

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