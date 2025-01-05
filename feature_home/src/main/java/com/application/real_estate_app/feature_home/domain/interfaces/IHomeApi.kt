package com.application.real_estate_app.feature_home.domain.interfaces

import com.application.real_estate_app.core.data_utils.models.Property


interface IHomeApi {
    // Get Property by Id
    suspend fun getPropertyById(propertyId: String): Property?

    // Fetch Liked Properties
    suspend fun fetchLikedProperties(userId: String): List<Property>

    // Fetch Properties Paginated
    suspend fun fetchPropertiesPaginated(
        lastVisible: String?,
        pageSize: Int
    ): Pair<List<Property>, String?>

    // Toggle Like Property
    suspend fun toggleLikeProperty(userId: String, propertyId: String): Boolean
}