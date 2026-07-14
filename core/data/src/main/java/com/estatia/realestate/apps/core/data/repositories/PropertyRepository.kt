package com.estatia.realestate.apps.core.data.repositories

import android.net.Uri
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper.toCacheEntities
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestorePropertyMapper
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestorePropertyMapper.toDomainModels
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestorePropertyMapper.toDomainOrNull
import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PropertyRepository @Inject constructor(
    private val localDataSource: IPropertyLocalDataSource,
    private val remoteDataSource: IPropertyRemoteDatasource
) : IPropertyRepository {

    // ─────────────────────────────────────────────
    // Local Draft Operations (UNCHANGED - internal entity OK)
    // ─────────────────────────────────────────────

    override suspend fun saveDraft(draft: PropertyDraftEntity): Long {
        return localDataSource.saveDraft(draft)
    }

    override suspend fun getAllDrafts(): List<PropertyDraftEntity> {
        return localDataSource.getAllDrafts()
    }

    override suspend fun getDraftById(draftId: Int): PropertyDraftEntity? {
        return localDataSource.getDraftById(draftId)
    }

    override suspend fun deleteDraft(draftId: Int) {
        localDataSource.deleteDraft(draftId)
    }

    override suspend fun clearAllDrafts() {
        localDataSource.clearAllDrafts()
    }

    // ─────────────────────────────────────────────
    // Remote state
    // ─────────────────────────────────────────────

    override val uploadStatus: StateFlow<Boolean>
        get() = remoteDataSource.uploadStatus

    override val uploadError: StateFlow<String?>
        get() = remoteDataSource.uploadError

    // ─────────────────────────────────────────────
    // Remote operations (FIXED mapping direction)
    // ─────────────────────────────────────────────

    override suspend fun uploadProperty(
        property: PropertyDomainModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean? {
        val entity = FirestorePropertyMapper.toEntity(property)

        return remoteDataSource.uploadProperty(
            entity,
            imageUris,
            videoUris,
            onFailure
        )
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.updateProperty(propertyId, updates, onFailure)
    }

    override suspend fun getPropertyById(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): PropertyDomainModel? {
        return remoteDataSource.getPropertyById(propertyId, onFailure)
            .toDomainOrNull()
    }

    override suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<PropertyDomainModel> {
        return remoteDataSource.fetchLikedProperties(userId, onFailure)
            ?.toDomainModels()
            ?: emptyList()
    }

    override suspend fun likeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.likeProperty(userId, propertyId, onFailure)
    }

    override suspend fun unlikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.unlikeProperty(userId, propertyId, onFailure)
    }

    override suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<PropertyDomainModel>, String?> {

        val result = remoteDataSource.fetchPropertiesPaginated(
            lastVisible,
            pageSize,
            onFailure
        )

        val domain = result.first.toDomainModels()

        return domain to result.second
    }

    // ─────────────────────────────────────────────
    // Cache-aware fetch (FIXED consistency)
    // ─────────────────────────────────────────────

    suspend fun fetchProperties(
        forceRefresh: Boolean = false,
        onFailure: (Exception) -> Unit
    ): List<PropertyDomainModel> {

        val cachedEntities = localDataSource.getCachedProperties()

        if (cachedEntities.isNotEmpty() && !forceRefresh) {
            return cachedEntities.map(RoomPropertyMapper::toDomain)
        }

        return try {

            val remoteEntities = remoteDataSource.fetchPropertiesPaginated(
                lastVisible = null,
                pageSize = 50,
                onFailure = onFailure
            ).first

            val domainModels = remoteEntities.toDomainModels()

            val cacheEntities = domainModels.toCacheEntities()

            localDataSource.cacheProperties(cacheEntities)

            domainModels

        } catch (e: Exception) {
            onFailure(e)
            cachedEntities.map(RoomPropertyMapper::toDomain)
        }
    }

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<PropertyDomainModel> {
        return remoteDataSource.searchProperties(query, limit, onFailure)
            .toDomainModels()
    }

    override suspend fun deleteProperty(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean {
        return remoteDataSource.deleteProperty(propertyId, onFailure)
    }
}
