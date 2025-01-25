package com.application.real_estate_app.feature_search.domain.interfaces

interface ILocalDataSource {

    suspend fun saveSearchQuery(query: String)

    suspend fun getSearchHistory(): List<String>

    suspend fun clearSearchHistory()
}