package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.data.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.data.mappers.RemotePropertyMapper.toDomainModels
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
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
    ): List<PropertyDomainModel> {
        localDataSource.saveSearchQuery(query) // Cache the search query locally
        return remoteDataSource.searchProperties(query, limit, onFailure).toDomainModels()
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
