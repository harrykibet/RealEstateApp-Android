package com.application.real_estate_app.feature_favorites.domain.interfaces

import com.application.real_estate_app.core.data_utils.models.Property

interface IFavoritesApi {

    // Fetch property by ID
    suspend fun getPropertyById(propertyId: String): Property?

    // Fetch Liked Properties
    suspend fun fetchLikedProperties(userId: String): List<Property>

    // Toggle Like Property
    suspend fun toggleLikeProperty(userId: String, propertyId: String): Boolean
}