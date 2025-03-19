package com.application.real_estate_app.feature_search.domain.interfaces

import com.application.real_estate_app.core_model.Property

interface IRemoteDataSource {
    // Search Properties
    suspend fun searchProperties(query: String, limit: Int, onFailure: (Exception) -> Unit): List<Property>
}