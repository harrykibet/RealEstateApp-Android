package com.application.real_estate_app.core_data.interfaces

import com.application.real_estate_app.core_model.Property

interface ISearchRepository {

    suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property>

    suspend fun getSearchHistory(): List<String>

    suspend fun clearSearchHistory()
}
