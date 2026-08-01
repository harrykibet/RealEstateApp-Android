package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface ISearchLocalDataSource {

    suspend fun saveSearchQuery(
        query: String
    ): AppResult<Unit>

    suspend fun getSearchHistory(): AppResult<List<String>>

    suspend fun clearSearchHistory(): AppResult<Unit>
}
