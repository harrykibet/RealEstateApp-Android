package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.interfaces.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestorePropertyMapper
import com.estatia.realestate.apps.core.data.util.translateSearchFailures
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val remoteDataSource: ISearchRemoteDataSource,
    private val localDataSource: ISearchLocalDataSource,
    private val exceptionTranslator: IExceptionTranslator
) : ISearchRepository {

    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyDomainModel>> {
        localDataSource.saveSearchQuery(query) // Cache the search query locally (best effort)
        
        return remoteDataSource.searchProperties(query, limit)
            .map { entities ->
                entities.map(FirestorePropertyMapper::toDomain)
            }
            .translateSearchFailures(exceptionTranslator)
    }

    override suspend fun getSearchHistory(): AppResult<List<String>> {
        return localDataSource.getSearchHistory()
            .translateSearchFailures(exceptionTranslator)
    }

    override suspend fun clearSearchHistory(): AppResult<Unit> {
        return localDataSource.clearSearchHistory()
            .translateSearchFailures(exceptionTranslator)
    }

    override suspend fun getNearbyProperties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): AppResult<List<PropertyDomainModel>> {

        return remoteDataSource.getNearbyProperties(
            latitude = latitude,
            longitude = longitude,
            radiusKm = radiusKm
        )
            .map { entities ->
                entities.map(FirestorePropertyMapper::toDomain)
            }
            .translateSearchFailures(exceptionTranslator)
    }
}
