package com.estatia.realestate.apps.core.testing_network.fake.source

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException

/**
 * In-memory fake implementation of [ISearchRemoteDataSource] with scriptable chaos.
 */
class FakeSearchRemoteDataSource(
    private val concurrencyChaos: ConcurrencyChaosController = ConcurrencyChaosController(),
    private val lifecycleChaos: LifecycleChaosController = LifecycleChaosController()
) : ISearchRemoteDataSource {

    private val properties = ConcurrentHashMap<String, PropertyEntityModel>()
    private val nextBehavior = AtomicReference<DatabaseBehavior>(DatabaseBehavior.Success)

    fun setNextBehavior(behavior: DatabaseBehavior) {
        nextBehavior.set(behavior)
    }

    fun addProperty(property: PropertyEntityModel) {
        properties[property.id] = property
    }

    override suspend fun searchProperties(query: String, limit: Int): AppResult<List<PropertyEntityModel>> {
        checkChaos("search_properties")?.let { return it }
        
        val results = properties.values
            .filter { 
                (it.title?.contains(query, ignoreCase = true) ?: false) || 
                (it.description?.contains(query, ignoreCase = true) ?: false) 
            }
            .take(limit)
            
        return AppResult.Success(results)
    }

    override suspend fun getNearbyProperties(latitude: Double, longitude: Double, radiusKm: Double): AppResult<List<PropertyEntityModel>> {
        checkChaos("get_nearby_properties")?.let { return it }
        // Simple mock implementation: returns all properties
        return AppResult.Success(properties.values.toList())
    }

    private suspend fun checkChaos(point: String): AppResult<Nothing>? {
        try {
            lifecycleChaos.checkChaos()
            concurrencyChaos.checkChaos(point)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return AppResult.Error(DatabaseException.Unknown(e))
        }

        val behavior = nextBehavior.getAndSet(DatabaseBehavior.Success)
        
        return when (behavior) {
            DatabaseBehavior.Unavailable -> AppResult.Error(DatabaseException.Unavailable)
            DatabaseBehavior.Locked -> AppResult.Error(DatabaseException.Unknown(java.io.IOException("Database locked (Chaos)")))
            DatabaseBehavior.Success -> null
            else -> null // Other behaviors can be added as needed
        }
    }
}
