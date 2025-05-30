package com.estatia.realestate.apps.core.database.sources

import com.estatia.realestate.apps.core.database.dao.SearchHistoryDao
import com.estatia.realestate.apps.core.database.entities.SearchHistoryEntity
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import javax.inject.Inject

class SearchLocalDataSource @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao // DAO injected via DI
) : ISearchLocalDataSource {

    // Save a new search query
    override suspend fun saveSearchQuery(query: String) {
        val searchEntity = SearchHistoryEntity(query = query)
        searchHistoryDao.insertSearchQuery(searchEntity)
        searchHistoryDao.maintainSearchHistoryLimit() // Ensure only 10 entries are kept
    }

    // Retrieve the last 10 search queries
    override suspend fun getSearchHistory(): List<String> {
        return searchHistoryDao.getSearchHistory().map { it.query }
    }

    // Clear all search history
    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearSearchHistory()
    }
}
