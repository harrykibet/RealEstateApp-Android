package com.estatia.realestate.apps.core.network.sources.aws

import android.net.Uri
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import javax.inject.Inject

/**
 * AWS implementation of [IPropertyRemoteDatasource] (Skeleton).
 * This will use AWS AppSync/S3 for properties in the future.
 */
internal class AwsPropertyRemoteDataSource @Inject constructor() : IPropertyRemoteDatasource {

    override suspend fun uploadProperty(property: PropertyEntityModel, imageUris: List<Uri>, videoUris: List<Uri>): AppResult<String> =
        AppResult.Success("")

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> =
        AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.DatabaseException.NotFound)

    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> =
        AppResult.Success(emptyList())

    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun recordView(propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun recordShare(propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun fetchPropertiesPaginated(cursor: PropertyCursor?, pageSize: Int): AppResult<PropertyRemotePage> =
        AppResult.Success(PropertyRemotePage(emptyList(), null))
}
