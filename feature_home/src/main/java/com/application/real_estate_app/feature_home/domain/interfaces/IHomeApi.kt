package com.application.real_estate_app.feature_home.domain.interfaces

import com.application.real_estate_app.core.data_utils.data_models.Property


interface IHomeApi {
    // Get Property by Id
    suspend fun getPropertyById(propertyId: String, onFailure: (Exception) -> Unit): Property?

    // Fetch Liked Properties
    suspend fun fetchLikedProperties(userId: String, onFailure: (Exception) -> Unit): List<Property>

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int,
        onFailure: (Exception) -> Unit
    ): Pair<List<Property>, String?>

    // Toggle Like Property
    suspend fun toggleLikeProperty(userId: String, propertyId: String, onFailure: (Exception) -> Unit): Boolean
}