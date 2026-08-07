package com.estatia.realestate.apps.core.network.interfaces

import android.net.Uri
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage

/**
 * Remote data source for managing property listings and interactions.
 */
interface IPropertyRemoteDatasource {

    /**
     * Uploads a new property listing along with its associated media files.
     */
    suspend fun uploadProperty(
        property: PropertyEntityModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String>

    /**
     * Updates an existing property listing.
     */
    suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit>

    /**
     * Deletes a property listing by its unique ID.
     */
    suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit>

    /**
     * Fetches a single property by its ID.
     */
    suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyEntityModel>

    /**
     * Fetches a list of properties liked by a specific user.
     */
    suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyEntityModel>>

    /**
     * Searches for properties based on a text query.
     */
    suspend fun searchProperties(
        query: String,
        limit: Int
    ): AppResult<List<PropertyEntityModel>>

    /**
     * Registers a "like" action for a user on a property.
     */
    suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    /**
     * Removes a "like" action for a user on a property.
     */
    suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    /**
     * Fetches properties using pagination.
     * 
     * @param cursor The cursor for the next page, or null for the first page.
     * @param pageSize Number of items to fetch per page.
     * @return A [PropertyRemotePage] containing the list of items and the next cursor.
     */
    suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage>
}
