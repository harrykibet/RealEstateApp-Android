package com.estatia.realestate.apps.core.testing.fake.source

import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyUpdateFields
import com.estatia.realestate.apps.core.network.db_entities.PropertyContactEntity
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory fake implementation of [IPropertyRemoteDatasource].
 */
class FakePropertyRemoteDataSource : IPropertyRemoteDatasource {

    private val storage = ConcurrentHashMap<String, PropertyEntityModel>()

    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        contactInfo: PropertyContactEntity,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> {
        val id = property.id.ifBlank { java.util.UUID.randomUUID().toString() }
        storage[id] = property.copy(id = id, contact = contactInfo)
        return AppResult.Success(id)
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: PropertyUpdateFields
    ): AppResult<Unit> {
        val existing = storage[propertyId] ?: return AppResult.Error(DatabaseException.NotFound)
        storage[propertyId] = existing.copy(
            title = updates.title ?: existing.title,
            description = updates.description ?: existing.description
        )
        return AppResult.Success(Unit)
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> {
        storage.remove(propertyId)
        return AppResult.Success(Unit)
    }

    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> {
        return storage[propertyId]?.let { AppResult.Success(it) } 
            ?: AppResult.Error(DatabaseException.NotFound)
    }

    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> {
        return AppResult.Success(storage.values.toList())
    }

    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun recordView(propertyId: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun recordShare(propertyId: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun fetchPropertiesPaginated(
        userId: String?,
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage> {
        return AppResult.Success(PropertyRemotePage(storage.values.toList(), null))
    }
}
