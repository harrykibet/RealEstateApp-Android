package com.application.real_estate_app.feature_search.domain.interfaces

import com.application.real_estate_app.core.data_utils.data_models.Property

interface ISearchApi {
    // Search Properties
    suspend fun searchProperties(query: String, limit: Int): List<Property>
}