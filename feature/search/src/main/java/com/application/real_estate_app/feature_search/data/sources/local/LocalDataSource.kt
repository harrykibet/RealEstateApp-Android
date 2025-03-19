package com.application.real_estate_app.feature_search.data.sources.local

import com.application.real_estate_app.feature_search.data.dao.SearchHistoryDao
import com.application.real_estate_app.feature_search.data.entities.SearchHistoryEntity
import com.application.real_estate_app.feature_search.domain.interfaces.ILocalDataSource
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao // DAO injected via DI
) : ILocalDataSource {

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
