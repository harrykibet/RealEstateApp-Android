package com.estatia.realestate.apps.core.domain.interfaces

import android.net.Uri
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.model.property.PropertyDraftDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyPage

/**
 * Domain-level repository for property management.
 * Orchestrates data between remote sources and local storage (drafts).
 */
interface IPropertyRepository {
    /**
     * Saves a property draft to local storage.
     */
    suspend fun saveDraft(draft: PropertyDraftDomainModel): AppResult<Long>

    /**
     * Retrieves all saved property drafts.
     */
    suspend fun getAllDrafts(): AppResult<List<PropertyDraftDomainModel>>

    /**
     * Retrieves a specific property draft by ID.
     */
    suspend fun getDraftById(draftId: Long): AppResult<PropertyDraftDomainModel?>

    /**
     * Deletes a property draft.
     */
    suspend fun deleteDraft(draftId: Long): AppResult<Unit>

    /**
     * Clears all saved property drafts.
     */
    suspend fun clearAllDrafts(): AppResult<Unit>

    /**
     * Uploads a property listing to the remote server.
     */
    suspend fun uploadProperty(
        property: PropertyDomainModel,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String>

    /**
     * Updates an existing remote property listing.
     */
    suspend fun updateProperty(
        propertyId: String,
        updates: Map<String, Any>
    ): AppResult<Unit>

    /**
     * Deletes a remote property listing.
     */
    suspend fun deleteProperty(
        propertyId: String
    ): AppResult<Unit>

    /**
     * Fetches a single property by ID.
     */
    suspend fun getPropertyById(
        propertyId: String
    ): AppResult<PropertyDomainModel>

    /**
     * Fetches multiple properties by their IDs.
     */
    suspend fun getPropertiesByIds(
        propertyIds: List<String>
    ): AppResult<List<PropertyDomainModel>>

    /**
     * Fetches properties liked by a specific user.
     */
    suspend fun fetchLikedProperties(
        userId: String
    ): AppResult<List<PropertyDomainModel>>

    /**
     * Registers a like action for a property.
     */
    suspend fun likeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    /**
     * Removes a like action for a property.
     */
    suspend fun unlikeProperty(
        userId: String,
        propertyId: String
    ): AppResult<Unit>

    /**
     * Increments the view count for a property.
     */
    suspend fun recordView(propertyId: String): AppResult<Unit>

    /**
     * Increments the share count for a property.
     */
    suspend fun recordShare(propertyId: String): AppResult<Unit>

    /**
     * Fetches properties using paginated results.
     */
    suspend fun fetchPropertiesPaginated(
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyPage>

}
