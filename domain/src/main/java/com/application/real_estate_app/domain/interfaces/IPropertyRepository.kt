package com.application.real_estate_app.domain.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.application.real_estate_app.domain.models.Comment
import com.application.real_estate_app.domain.models.Property
import kotlinx.coroutines.flow.Flow

interface IPropertyRepository {

    // LiveData to monitor the upload status
    val uploadStatus: LiveData<Boolean>
    val uploadError: LiveData<String?>

    // Property Upload
    suspend fun uploadProperty(
        property: Property,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): Boolean

    // Update Property

    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Boolean

    // Delete Property
    suspend fun deleteProperty(propertyId: String): Boolean

    // Get Property by Id
    suspend fun getPropertyById(propertyId: String): Property?

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int
    ): Pair<List<Property>, String?>

    // Fetch Liked Properties
    suspend fun fetchLikedProperties(userId: String): List<Property>

    // Toggle Like Property
    suspend fun toggleLikeProperty(userId: String, propertyId: String): Boolean

    // Listen for Comments
    fun listenForComments(
        propertyId: String,
        onError: (Exception) -> Unit
    ): Flow<List<Comment?>>

    // Submit Comment
    suspend fun submitComment(
        propertyId: String,
        comment: Comment
    ): Boolean

    // Search Properties
    suspend fun searchProperties(query: String, limit: Int): List<Property>
}
