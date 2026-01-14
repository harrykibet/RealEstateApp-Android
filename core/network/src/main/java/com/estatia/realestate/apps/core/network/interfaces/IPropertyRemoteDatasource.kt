package com.estatia.realestate.apps.core.network.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.estatia.realestate.apps.core.model.property.Property

interface IPropertyRemoteDatasource {
    // LiveData to monitor the upload status
    val uploadStatus: LiveData<Boolean>
    val uploadError: LiveData<String?>

    suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>,
        onFailure: (Exception) -> Unit
    ): Boolean?

    // Update Property
    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>, onFailure: (Exception) -> Unit): Boolean

    // Delete Property
    suspend fun deleteProperty(propertyId: String, onFailure: (Exception) -> Unit): Boolean

    // Get Property by Id
    suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property?

    suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<Property>?

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<Property>, String?>

    suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property>

    suspend fun likeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean

    suspend fun unlikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean
}