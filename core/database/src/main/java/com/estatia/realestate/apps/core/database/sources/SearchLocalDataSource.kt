package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.dao.SearchCacheDao
import com.estatia.realestate.apps.core.database.dao.SearchHistoryDao
import com.estatia.realestate.apps.core.database.entities.SearchCacheEntity
import com.estatia.realestate.apps.core.database.entities.SearchHistoryEntity
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import javax.inject.Inject

private const val MAX_SEARCH_RESULTS = 50
private const val TARGET_SEARCH_RESULTS = 40

/**
 * Local data source for managing search history and cached results.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: Source of truth for local search history and transient query results.
 * - Concurrency: Thread-safe; delegates to [databaseExecutor].
 * - Resilience: Implements bounded history (10 items) and LRU cache trimming.
 * - Performance: Offloads all I/O to background threads.
 * - Observability: Tracks cache trim events.
 */
internal class SearchLocalDataSource @Inject constructor(
    private val historyDao: SearchHistoryDao,
    private val cacheDao: SearchCacheDao,
    private val metricsTracker: IMetricsTracker,
    private val databaseExecutor: ILocalDatabaseExecutor
) : ISearchLocalDataSource {


    // Save a new search query
    override suspend fun saveSearchQuery(
        query: String
    ): AppResult<Unit> =
        databaseExecutor.execute {

            historyDao.insertSearchQuery(
                SearchHistoryEntity(query = query)
            )

            historyDao.maintainSearchHistoryLimit()
        }


    // Retrieve the last 10 search queries
    override suspend fun getSearchHistory()
            : AppResult<List<String>> =
        databaseExecutor.execute {

            historyDao.getSearchHistory()
                .map(SearchHistoryEntity::query)
        }


    // Clear all search history
    override suspend fun clearSearchHistory()
            : AppResult<Unit> =
        databaseExecutor.execute {

            historyDao.clearSearchHistory()
        }

    override suspend fun cacheSearchResult(
        query: String,
        propertyIds: List<String>
    ): AppResult<Unit> =
        databaseExecutor.execute {
            cacheDao.insert(
                SearchCacheEntity(
                    query = query,
                    propertyIds = propertyIds,
                    timestamp = System.currentTimeMillis()
                )
            )
            if (cacheDao.count() > MAX_SEARCH_RESULTS) {
                metricsTracker.incrementCounter("database.cache.search_trim")
                cacheDao.trim(TARGET_SEARCH_RESULTS)
            }
        }

    override suspend fun getCachedSearchResult(
        query: String
    ): AppResult<List<String>?> =
        databaseExecutor.execute {
            cacheDao.get(query)?.propertyIds
        }
}
