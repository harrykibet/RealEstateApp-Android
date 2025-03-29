package com.application.real_estate_app.core_network.interfaces

import com.application.real_estate_app.core_model.Property
import com.google.android.gms.maps.GoogleMap

interface ISearchRemoteDataSource {
    // Search Properties
    suspend fun searchProperties(query: String, limit: Int, onFailure: (Exception) -> Unit): List<Property>
    suspend fun loadNearbyProperties(map: GoogleMap, userLat: Double, userLng: Double): Boolean
}