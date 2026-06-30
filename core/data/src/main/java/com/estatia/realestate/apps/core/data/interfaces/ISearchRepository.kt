package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.google.android.gms.maps.GoogleMap

interface ISearchRepository {

    suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<PropertyDomainModel>

    suspend fun getSearchHistory(): List<String>

    suspend fun clearSearchHistory()

    suspend fun loadNearbyProperties(map: GoogleMap, userLat: Double, userLng: Double): Boolean
}
