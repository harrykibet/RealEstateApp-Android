package com.estatia.realestate.apps.core.network.interfaces

import android.net.Uri
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.common.errors.Result
import kotlinx.coroutines.flow.StateFlow

interface IPropertyRemoteDatasource {
    // LiveData to monitor the upload status
    val uploadStatus: StateFlow<Boolean>

    val uploadError: StateFlow<String?>

    suspend fun uploadProperty(
        property: PropertyEntityModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): Result<String>

    // Update Property
    suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): Result<Unit>

    // Delete Property
    suspend fun deleteProperty(
        propertyId: String
    ): Result<Unit>

    // Get Property by id
    suspend fun getPropertyById(
        propertyId: String
    ): Result<PropertyEntityModel>

    suspend fun fetchLikedProperties(
        userId: String
    ): Result<List<PropertyEntityModel>>

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int
    ): Result<Pair<List<PropertyEntityModel>, String?>>

    suspend fun searchProperties(
        query: String,
        limit: Int
    ): Result<List<PropertyEntityModel>>

    suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): Result<Unit>

    suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): Result<Unit>
}