package com.estatia.realestate.apps.core.testing_network.fake.source

import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyUpdateFields
import com.estatia.realestate.apps.core.network.db_entities.PropertyContactEntity
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory fake implementation of [IPropertyRemoteDatasource] with scriptable chaos.
 * Supports realistic pagination logic using [PropertyCursor].
 */
class FakePropertyRemoteDataSource : IPropertyRemoteDatasource {

    private val storage = ConcurrentHashMap<String, PropertyEntityModel>()
    private var nextBehavior: DatabaseBehavior = DatabaseBehavior.Success

    fun setNextBehavior(behavior: DatabaseBehavior) {
        nextBehavior = behavior
    }

    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        contactInfo: PropertyContactEntity,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> {
        checkChaos()
        val id = property.id.ifBlank { java.util.UUID.randomUUID().toString() }
        storage[id] = property.copy(id = id, contact = contactInfo)
        return AppResult.Success(id)
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: PropertyUpdateFields
    ): AppResult<Unit> {
        checkChaos()
        val existing = storage[propertyId] ?: return AppResult.Error(DatabaseException.NotFound)
        storage[propertyId] = existing.copy(
            title = updates.title ?: existing.title,
            description = updates.description ?: existing.description
        )
        return AppResult.Success(Unit)
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> {
        checkChaos()
        storage.remove(propertyId)
        return AppResult.Success(Unit)
    }

    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> {
        checkChaos()
        return storage[propertyId]?.let { AppResult.Success(it) } 
            ?: AppResult.Error(DatabaseException.NotFound)
    }

    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> {
        checkChaos()
        return AppResult.Success(storage.values.toList())
    }

    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> {
        checkChaos()
        return AppResult.Success(Unit)
    }

    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> {
        checkChaos()
        return AppResult.Success(Unit)
    }

    override suspend fun recordView(propertyId: String): AppResult<Unit> {
        checkChaos()
        return AppResult.Success(Unit)
    }

    override suspend fun recordShare(propertyId: String): AppResult<Unit> {
        checkChaos()
        return AppResult.Success(Unit)
    }

    override suspend fun fetchPropertiesPaginated(
        userId: String?,
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage> {
        checkChaos()

        val allProperties = storage.values
            .sortedByDescending { it.createdAt }

        val startIndex = if (cursor != null) {
            val index = allProperties.indexOfFirst { it.id == cursor.documentId }
            if (index == -1) return AppResult.Error(DatabaseException.NotFound)
            index + 1
        } else {
            0
        }

        val pageItems = allProperties.drop(startIndex).take(pageSize)
        val lastItem = pageItems.lastOrNull()

        val nextCursor = if (pageItems.size == pageSize && startIndex + pageSize < allProperties.size) {
            lastItem?.let { PropertyCursor(it.createdAt ?: 0L, it.id) }
        } else {
            null
        }

        return AppResult.Success(PropertyRemotePage(pageItems, nextCursor))
    }

    private fun checkChaos() {
        when (nextBehavior) {
            DatabaseBehavior.Unavailable -> throw java.io.IOException("Remote service unavailable (Chaos)")
            DatabaseBehavior.Corrupted -> throw java.lang.RuntimeException("Data corruption (Chaos)")
            DatabaseBehavior.Success -> Unit
            else -> Unit
        }
        nextBehavior = DatabaseBehavior.Success
    }
}
