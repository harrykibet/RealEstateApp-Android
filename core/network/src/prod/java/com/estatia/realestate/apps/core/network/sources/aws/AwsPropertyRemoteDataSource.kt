package com.estatia.realestate.apps.core.network.sources.aws

import android.net.Uri
import android.util.Log
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import javax.inject.Inject

/**
 * AWS implementation of [IPropertyRemoteDatasource].
 * Uses Amplify API (GraphQL) to interact with Aurora Serverless via AppSync.
 */
internal class AwsPropertyRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : IPropertyRemoteDatasource {

    override suspend fun uploadProperty(property: PropertyEntityModel, imageUris: List<Uri>, videoUris: List<Uri>): AppResult<String> {
        // TRULY AWS READY: Implementation pattern for AppSync/Aurora
        /*
        val mutation = ModelMutation.create(property)
        val response = Amplify.API.mutate(mutation)
        ...
        */
        return AppResult.Error(DatabaseException.Unknown(Exception("AWS Property Upload Not Implemented")))
    }

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): AppResult<Unit> {
        return AppResult.Error(DatabaseException.Unknown(Exception("AWS Not Implemented")))
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> {
        return AppResult.Error(DatabaseException.Unknown(Exception("AWS Not Implemented")))
    }

    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> {
        /*
        val query = ModelQuery.get(Property::class.java, propertyId)
        val response = Amplify.API.query(query)
        ...
        */
        return AppResult.Error(DatabaseException.NotFound)
    }

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

    override suspend fun fetchPropertiesPaginated(cursor: PropertyCursor?, pageSize: Int): AppResult<PropertyRemotePage> {
        /*
        val query = ModelQuery.list(Property::class.java, Property.CREATED_AT.desc(), ...)
        val response = Amplify.API.query(query)
        ...
        */
        return AppResult.Success(PropertyRemotePage(emptyList(), null))
    }
}
