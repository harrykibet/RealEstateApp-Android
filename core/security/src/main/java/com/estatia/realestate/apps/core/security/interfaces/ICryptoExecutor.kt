package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException

interface ICryptoExecutor {
    suspend fun <T> execute(
        defaultException: SecurityException,
        operation: suspend () -> T
    ): AppResult<T>
}
