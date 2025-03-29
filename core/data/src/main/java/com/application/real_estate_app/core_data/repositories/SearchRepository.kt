package com.application.real_estate_app.core_data.repositories

import com.application.real_estate_app.core_data.interfaces.ISearchRepository
import com.application.real_estate_app.core_model.Property
import com.application.real_estate_app.core_database.interfaces.ISearchLocalDataSource
import com.application.real_estate_app.core_network.interfaces.ISearchRemoteDataSource
import com.google.android.gms.maps.GoogleMap
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val remoteDataSource: ISearchRemoteDataSource,
    private val localDataSource: ISearchLocalDataSource
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

    override suspend fun loadNearbyProperties(map: GoogleMap, userLat: Double, userLng: Double): Boolean {
        return remoteDataSource.loadNearbyProperties(map, userLat, userLng)
    }
}
