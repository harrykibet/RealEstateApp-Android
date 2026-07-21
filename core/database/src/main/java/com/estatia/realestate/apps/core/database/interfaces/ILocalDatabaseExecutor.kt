package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface ILocalDatabaseExecutor {

    suspend fun <T> execute(
        operation: suspend () -> T
    ): AppResult<T>
}