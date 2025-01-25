package com.application.real_estate_app.feature_search.domain.interfaces

import com.application.real_estate_app.core.domain.models.Property

interface ISearchRepository {

    suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property>

    suspend fun getSearchHistory(): List<String>

    suspend fun clearSearchHistory()
}
