package com.estatia.realestate.apps.core.database.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.entities.SearchCacheEntity

interface ISearchLocalDataSource {

    suspend fun saveSearchQuery(
        query: String
    ): AppResult<Unit>

    suspend fun getSearchHistory(): AppResult<List<String>>

    suspend fun clearSearchHistory(): AppResult<Unit>

    suspend fun cacheSearchResult(
        query: String,
        propertyIds: List<String>
    ): AppResult<Unit>

    suspend fun getCachedSearchResult(
        query: String
    ): AppResult<List<String>?>
}
