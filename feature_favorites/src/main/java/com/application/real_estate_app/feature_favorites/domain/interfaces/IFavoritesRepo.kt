package com.application.real_estate_app.feature_favorites.domain.interfaces

import com.application.real_estate_app.core.domain.models.Property

interface IFavoritesRepo {

    suspend fun fetchLikedProperties(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<Property>?

    suspend fun getPropertyById(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Property?

    suspend fun toggleLikeProperty(
        userId: String,
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Boolean
}