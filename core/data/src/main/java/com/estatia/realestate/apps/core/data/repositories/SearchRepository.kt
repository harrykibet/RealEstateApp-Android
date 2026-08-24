package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.getOrNull
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.data.mappers.remote.RemotePropertyMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper.toCacheEntities
import com.estatia.realestate.apps.core.data.util.translateSearchFailures
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.database.interfaces.ISearchLocalDataSource
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject

/**
 * Repository for handling property search and discovery.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: Search history and caching handled by [ISearchLocalDataSource].
 * - Concurrency: Thread-safe repository calls.
 * - Resilience: Transparent fallback to local search cache when remote is unavailable.
 * - Observability: Tracks search latency, cache hit/miss ratio, and failure types.
 */
/**
 * Repository for handling property search and discovery.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Ownership: Search history and caching handled by [ISearchLocalDataSource].
 * - Concurrency: Thread-safe repository calls.
 * - Resilience: Transparent fallback to local search cache when remote is unavailable.
 * - Observability: Tracks search latency, cache hit/miss ratio, and failure types.
 */
internal class SearchRepository @Inject constructor(
    private val remoteDataSource: ISearchRemoteDataSource,
    private val searchLocalDataSource: ISearchLocalDataSource,
    private val propertyLocalDataSource: IPropertyLocalDataSource,
    private val engagementRepository: IEngagementRepository,
    private val metricsTracker: IMetricsTracker,
    private val exceptionTranslator: IExceptionTranslator
) : ISearchRepository {

    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyDomainModel>> {
        val startTime = System.currentTimeMillis()
        searchLocalDataSource.saveSearchQuery(query) 
        // 🏎️ Report engagement signal for personalization
        engagementRepository.reportSearch(query)

        // 1. Try search cache
        val cachedIds = searchLocalDataSource.getCachedSearchResult(query).getOrNull()
        if (!cachedIds.isNullOrEmpty()) {
            val cachedProperties = propertyLocalDataSource.getCachedPropertiesByIds(cachedIds).getOrNull()
            if (!cachedProperties.isNullOrEmpty()) {
                metricsTracker.incrementCounter("search.cache.hit")
                return AppResult.Success(cachedProperties.map(RoomPropertyMapper::toDomain))
            }
        }

        metricsTracker.incrementCounter("search.cache.miss")

        // 2. Remote search
        return remoteDataSource.searchProperties(query, limit)
            .map { entities ->
                val domainModels = entities.map(RemotePropertyMapper::toDomain)
                
                // 3. Update cache
                searchLocalDataSource.cacheSearchResult(query, domainModels.map { it.id.value })
                propertyLocalDataSource.cacheProperties(domainModels.toCacheEntities())

                domainModels
            }
            .translateSearchFailures(exceptionTranslator)
            .also { result ->
                val duration = System.currentTimeMillis() - startTime
                metricsTracker.trackDuration("search.latency", duration.milliseconds)
                if (result is AppResult.Error) {
                    metricsTracker.incrementCounter("search.failure")
                }
            }
    }

    override suspend fun getSearchHistory(): AppResult<List<String>> {
        return searchLocalDataSource.getSearchHistory()
            .translateSearchFailures(exceptionTranslator)
    }

    override suspend fun clearSearchHistory(): AppResult<Unit> {
        return searchLocalDataSource.clearSearchHistory()
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
                entities.map(RemotePropertyMapper::toDomain)
            }
            .translateSearchFailures(exceptionTranslator)
    }
}
