package com.application.real_estate_app.feature_search.data.repositories

import com.application.real_estate_app.core.domain.models.Property
import com.application.real_estate_app.feature_search.domain.interfaces.ILocalDataSource
import com.application.real_estate_app.feature_search.domain.interfaces.IRemoteDataSource
import com.application.real_estate_app.feature_search.domain.interfaces.ISearchRepository
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource
) : ISearchRepository {

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property> {
        localDataSource.saveSearchQuery(query) // Cache the search query locally
        return remoteDataSource.searchProperties(query, limit, onFailure)
    }

    override suspend fun getSearchHistory(): List<String> {
        return localDataSource.getSearchHistory()
    }

    override suspend fun clearSearchHistory() {
        localDataSource.clearSearchHistory()
    }
}
