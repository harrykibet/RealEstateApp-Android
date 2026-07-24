package com.estatia.realestate.apps.core.data.repositories

import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestorePropertyMapper
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.model.property.PropertyDraftDomainModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator
import com.estatia.realestate.apps.core.data.util.translatePropertyFailures
import com.estatia.realestate.apps.core.data.mappers.room.RoomPropertyDraftMapper
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyPage
import javax.inject.Inject

class PropertyRepository @Inject constructor(
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
                properties.map(
                    FirestorePropertyMapper::toDomain
                )
            }
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyPage> {

        return remoteDataSource
            .fetchPropertiesPaginated(
                cursor,
                pageSize
            )
            .map { remotePage ->
                PropertyPage(
                    properties =
                    remotePage.properties.map {
                        FirestorePropertyMapper.toDomain(it)
                    },
                    cursor =
                    remotePage.cursor
                )
            }
            .translatePropertyFailures(
                exceptionTranslator
            )
    }

    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyDomainModel>> {

        return remoteDataSource
            .searchProperties(
                query,
                limit
            )
            .map { entities ->
                entities.map(
                    FirestorePropertyMapper::toDomain
                )
            }
            .translatePropertyFailures(
                exceptionTranslator
            )
    }
}
