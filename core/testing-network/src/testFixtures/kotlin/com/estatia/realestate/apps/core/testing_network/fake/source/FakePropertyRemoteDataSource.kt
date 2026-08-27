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
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory fake implementation of [IPropertyRemoteDatasource] with scriptable chaos.
 * Supports realistic pagination logic using [PropertyCursor].
 */
class FakePropertyRemoteDataSource(
    private val concurrencyChaos: ConcurrencyChaosController = ConcurrencyChaosController(),
    private val lifecycleChaos: LifecycleChaosController = LifecycleChaosController()
) : IPropertyRemoteDatasource {

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
        checkChaos("upload_property")?.let { return it }
        val id = property.id.ifBlank { java.util.UUID.randomUUID().toString() }
        storage[id] = property.copy(id = id, contact = contactInfo)
        return AppResult.Success(id)
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: PropertyUpdateFields
    ): AppResult<Unit> {
        checkChaos("update_property")?.let { return it }
        val existing = storage[propertyId] ?: return AppResult.Error(DatabaseException.NotFound)
        storage[propertyId] = existing.copy(
            title = updates.title ?: existing.title,
            description = updates.description ?: existing.description
        )
        return AppResult.Success(Unit)
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> {
        checkChaos("delete_property")?.let { return it }
        storage.remove(propertyId)
        return AppResult.Success(Unit)
    }

    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> {
        checkChaos("get_property_by_id")?.let { return it }
        return storage[propertyId]?.let { AppResult.Success(it) } 
            ?: AppResult.Error(DatabaseException.NotFound)
    }

    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> {
        checkChaos("fetch_liked_properties")?.let { return it }
        return AppResult.Success(storage.values.toList())
    }

    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> {
        checkChaos("like_property")?.let { return it }
        return AppResult.Success(Unit)
    }

    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> {
        checkChaos("unlike_property")?.let { return it }
        return AppResult.Success(Unit)
    }

    override suspend fun recordView(propertyId: String): AppResult<Unit> {
        checkChaos("record_view")?.let { return it }
        return AppResult.Success(Unit)
    }

    override suspend fun recordShare(propertyId: String): AppResult<Unit> {
        checkChaos("record_share")?.let { return it }
        return AppResult.Success(Unit)
    }

    override suspend fun fetchPropertiesPaginated(
        userId: String?,
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage> {
        checkChaos("fetch_properties_paginated")?.let { return it }

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

    private suspend fun checkChaos(point: String): AppResult<Nothing>? {
        try {
            lifecycleChaos.checkChaos()
            concurrencyChaos.checkChaos(point)
        } catch (e: Exception) {
            return AppResult.Error(DatabaseException.Unknown(e))
        }

        val behavior = nextBehavior
        nextBehavior = DatabaseBehavior.Success
        
        return when (behavior) {
            DatabaseBehavior.Unavailable -> AppResult.Error(DatabaseException.Unavailable)
            DatabaseBehavior.Corrupted -> AppResult.Error(DatabaseException.CorruptedDatabase(java.lang.RuntimeException("Chaos")))
            DatabaseBehavior.Locked -> AppResult.Error(DatabaseException.Unknown(java.io.IOException("Database is locked (Chaos)")))
            DatabaseBehavior.ConstraintViolation -> AppResult.Error(DatabaseException.ConstraintViolation(java.lang.IllegalArgumentException("Chaos")))
            DatabaseBehavior.DiskFull -> AppResult.Error(DatabaseException.StorageFull(java.io.IOException("Chaos")))
            DatabaseBehavior.MigrationFailure -> AppResult.Error(DatabaseException.Unknown(java.lang.IllegalStateException("Migration failed (Chaos)")))
            DatabaseBehavior.SchemaMismatch -> AppResult.Error(DatabaseException.Unknown(java.lang.IllegalStateException("Schema mismatch (Chaos)")))
            DatabaseBehavior.DuplicateData -> AppResult.Error(DatabaseException.AlreadyExists)
            DatabaseBehavior.ConcurrentWrites -> AppResult.Error(DatabaseException.Unknown(java.util.ConcurrentModificationException("Concurrent write detected (Chaos)")))
            DatabaseBehavior.TransactionFailure -> AppResult.Error(DatabaseException.TransactionFailed)
            DatabaseBehavior.PartialTransaction -> AppResult.Error(DatabaseException.TransactionFailed)
            DatabaseBehavior.ProcessDeathDuringTransaction -> AppResult.Error(DatabaseException.Unknown(java.lang.RuntimeException("Process died during transaction (Chaos)")))
            DatabaseBehavior.VeryLargeDataset -> null
            DatabaseBehavior.EmptyDataset -> {
                storage.clear()
                null
            }
            DatabaseBehavior.Success -> null
        }
    }
}
