package com.estatia.realestate.apps.core.network.sources.aws

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import javax.inject.Inject

/**
 * AWS implementation of [ISearchRemoteDataSource] (Skeleton).
 * This will use AWS OpenSearch for properties in the future.
 */
internal class AwsSearchRemoteDataSource @Inject constructor() : ISearchRemoteDataSource {
    override suspend fun searchProperties(query: String, limit: Int): AppResult<List<PropertyEntityModel>> = AppResult.Success(emptyList())
    override suspend fun getNearbyProperties(latitude: Double, longitude: Double, radiusKm: Double): AppResult<List<PropertyEntityModel>> = AppResult.Success(emptyList())
}
