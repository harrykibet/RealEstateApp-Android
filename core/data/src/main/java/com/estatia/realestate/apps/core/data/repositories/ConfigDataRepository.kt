package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.config.IConfigDataRepository
import com.estatia.realestate.apps.core.network.interfaces.IConfigRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import javax.inject.Inject

/**
 * Repository for managing raw remote configuration data.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Act as a thin bridge between the Domain Config layer and the Network layer.
 * - Concurrency: Stateless and thread-safe.
 * - Observability: Tracks remote config fetch success/failure.
 */
internal class ConfigDataRepository @Inject constructor(
    private val remoteDataSource: IConfigRemoteDataSource,
    private val metricsTracker: IMetricsTracker
) : IConfigDataRepository {

    override suspend fun fetchRemoteConfig(): AppResult<String?> {
        return remoteDataSource.fetchRemoteConfig().also { result ->
            if (result is AppResult.Success) {
                metricsTracker.incrementCounter("config.fetch.success")
            } else {
                metricsTracker.incrementCounter("config.fetch.failure")
            }
        }
    }
}
