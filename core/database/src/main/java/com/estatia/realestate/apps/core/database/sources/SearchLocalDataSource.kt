package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.database.dao.SearchHistoryDao
import com.estatia.realestate.apps.core.database.entities.SearchHistoryEntity
import com.estatia.realestate.apps.core.database.interfaces.ILocalDatabaseExecutor
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import javax.inject.Inject

class SearchLocalDataSource @Inject constructor(
    private val dao: SearchHistoryDao,
    private val databaseExecutor: ILocalDatabaseExecutor
) : ISearchLocalDataSource {


    // Save a new search query
    override suspend fun saveSearchQuery(
        query: String
    ): AppResult<Unit> =
        databaseExecutor.execute {

            dao.insertSearchQuery(
                SearchHistoryEntity(query = query)
            )

            dao.maintainSearchHistoryLimit()
        }


    // Retrieve the last 10 search queries
    override suspend fun getSearchHistory()
            : AppResult<List<String>> =
        databaseExecutor.execute {

            dao.getSearchHistory()
                .map(SearchHistoryEntity::query)
        }


    // Clear all search history
    override suspend fun clearSearchHistory()
            : AppResult<Unit> =
        databaseExecutor.execute {

            dao.clearSearchHistory()
        }
}
