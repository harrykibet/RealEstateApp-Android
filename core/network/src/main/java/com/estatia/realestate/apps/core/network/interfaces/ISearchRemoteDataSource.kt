package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.model.property.Property
import com.google.android.gms.maps.GoogleMap

interface ISearchRemoteDataSource {
    // Search Properties
    suspend fun searchProperties(query: String, limit: Int, onFailure: (Exception) -> Unit): List<Property>
    suspend fun loadNearbyProperties(map: GoogleMap, userLat: Double, userLng: Double): Boolean
}