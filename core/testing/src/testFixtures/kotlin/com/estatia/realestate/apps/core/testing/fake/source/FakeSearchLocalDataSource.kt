package com.estatia.realestate.apps.core.testing.fake.source

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory fake implementation of [ISearchLocalDataSource].
 */
class FakeSearchLocalDataSource : ISearchLocalDataSource {

    private val searchHistory = mutableListOf<String>()
    private val searchCache = ConcurrentHashMap<String, List<String>>()

    override suspend fun saveSearchQuery(query: String): AppResult<Unit> {
        if (!searchHistory.contains(query)) {
            searchHistory.add(0, query)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun getSearchHistory(): AppResult<List<String>> {
        return AppResult.Success(searchHistory.toList())
    }

    override suspend fun clearSearchHistory(): AppResult<Unit> {
        searchHistory.clear()
        return AppResult.Success(Unit)
    }

    override suspend fun cacheSearchResult(query: String, propertyIds: List<String>): AppResult<Unit> {
        searchCache[query] = propertyIds
        return AppResult.Success(Unit)
    }

    override suspend fun getCachedSearchResult(query: String): AppResult<List<String>?> {
        return AppResult.Success(searchCache[query])
    }
}
