package com.estatia.realestate.apps.core.network.interfaces

import android.net.Uri
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage

interface IPropertyRemoteDatasource {

    suspend fun uploadProperty(
        property: PropertyEntityModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String>

    // Update Property
    suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit>

    // Delete Property
    suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit>

    // Get Property by id
    suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyEntityModel>

    suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyEntityModel>>

    suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyEntityModel>>

    suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage>
}