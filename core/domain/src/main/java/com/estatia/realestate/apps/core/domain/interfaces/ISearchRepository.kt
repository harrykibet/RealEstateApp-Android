package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

interface ISearchRepository {

    suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyDomainModel>>

    suspend fun getSearchHistory(): AppResult<List<String>>

    suspend fun clearSearchHistory(): AppResult<Unit>

    suspend fun getNearbyProperties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): AppResult<List<PropertyDomainModel>>
}
