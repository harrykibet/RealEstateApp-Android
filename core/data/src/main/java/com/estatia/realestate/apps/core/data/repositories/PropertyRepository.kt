package com.estatia.realestate.apps.core.data.repositories

import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.getOrNull
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.domain.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestorePropertyMapper
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.model.property.PropertyDraftDomainModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.interfaces.IExceptionTranslator
import com.estatia.realestate.apps.core.data.util.translatePropertyFailures
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyDraftMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyMapper.toCacheEntities
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyPage
import javax.inject.Inject

private const val MAX_CACHE_AGE_5_MIN = 5 * 60 * 1000L

internal class PropertyRepository @Inject constructor(
    private val localDataSource: IPropertyLocalDataSource,
    private val remoteDataSource: IPropertyRemoteDatasource,
    private val exceptionTranslator: IExceptionTranslator
) : IPropertyRepository {

    override suspend fun saveDraft(
        draft: PropertyDraftDomainModel
    ): AppResult<Long> {
        val entity = RoomPropertyDraftMapper.toEntity(draft)
        return localDataSource.saveDraft(entity)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun getAllDrafts(): AppResult<List<PropertyDraftDomainModel>> {
        return localDataSource.getAllDrafts()
            .map { entities ->
                entities.map(RoomPropertyDraftMapper::toDomain)
            }
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun getDraftById(
        draftId: Long
    ): AppResult<PropertyDraftDomainModel?> {
        return localDataSource.getDraftById(draftId)
            .map { entity ->
                RoomPropertyDraftMapper.toDomain(entity)
            }
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun deleteDraft(draftId: Long): AppResult<Unit> {
        return localDataSource.deleteDraft(draftId)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun clearAllDrafts(): AppResult<Unit> {
        return localDataSource.clearAllDrafts()
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun uploadProperty(
        property: PropertyDomainModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> {

        return remoteDataSource
            .uploadProperty(
                FirestorePropertyMapper.toEntity(property),
                imageUris,
                videoUris
            )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit> {

        return remoteDataSource
            .updateProperty(
                propertyId,
                updates
            )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit> {

        return remoteDataSource
            .deleteProperty(propertyId)
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyDomainModel> {

        return remoteDataSource
            .getPropertyById(propertyId)
            .map {
                FirestorePropertyMapper.toDomain(it)
            }
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun getPropertiesByIds(
        propertyIds: List<String>
    ): AppResult<List<PropertyDomainModel>> {
        return localDataSource.getCachedPropertiesByIds(propertyIds)
            .map { entities ->
                entities.map(RoomPropertyMapper::toDomain)
            }
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {

        return remoteDataSource
            .likeProperty(
                userId,
                propertyId
            )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit> {

        return remoteDataSource.unlikeProperty(
            userId,
            propertyId
        )
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyDomainModel>> {

        return remoteDataSource
            .fetchLikedProperties(userId)
            .map { properties ->
                val domainModels = properties.map(FirestorePropertyMapper::toDomain)
                // Mark as liked in local cache
                localDataSource.cacheProperties(domainModels.map { 
                    RoomPropertyMapper.toEntity(it).copy(isLiked = true)
                })
                domainModels
            }
            .translatePropertyFailures(
                exceptionTranslator
            ).let { result ->
                if (result is AppResult.Error) {
                    localDataSource.getCachedProperties().map { entities ->
                        entities.filter { it.isLiked }.map(RoomPropertyMapper::toDomain)
                    }
                } else result
            }
    }

    override suspend fun recordView(propertyId: String): AppResult<Unit> {
        return remoteDataSource.recordView(propertyId)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun recordShare(propertyId: String): AppResult<Unit> {
        return remoteDataSource.recordShare(propertyId)
            .translatePropertyFailures(exceptionTranslator)
    }

    override suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyPage> {

        // 1. Try to serve from cache if first page and not stale
        if (cursor == null) {
            val isStale = localDataSource.isCacheStale(MAX_CACHE_AGE_5_MIN).getOrNull() ?: true
            if (!isStale) {
                val cached = localDataSource.getCachedProperties().getOrNull()
                if (!cached.isNullOrEmpty()) {
                    return AppResult.Success(
                        PropertyPage(
                            properties = cached.map(RoomPropertyMapper::toDomain),
                            cursor = null // Cache only stores first page for now
                        )
                    )
                }
            }
        }

        // 2. Fetch from remote
        val remoteResult = remoteDataSource.fetchPropertiesPaginated(cursor, pageSize)

        return remoteResult.map { remotePage ->
            val domainProperties = remotePage.properties.map(FirestorePropertyMapper::toDomain)
            
            // 3. Update cache if it's the first page
            if (cursor == null) {
                localDataSource.cacheProperties(domainProperties.toCacheEntities())
            }

            PropertyPage(
                properties = domainProperties,
                cursor = remotePage.cursor
            )
        }.translatePropertyFailures(exceptionTranslator).let { result ->
            // 4. Fallback to cache on error if first page
            if (result is AppResult.Error && cursor == null) {
                val cached = localDataSource.getCachedProperties().getOrNull()
                if (!cached.isNullOrEmpty()) {
                    AppResult.Success(
                        PropertyPage(
                            properties = cached.map(RoomPropertyMapper::toDomain),
                            cursor = null
                        )
                    )
                } else result
            } else result
        }
    }
}
