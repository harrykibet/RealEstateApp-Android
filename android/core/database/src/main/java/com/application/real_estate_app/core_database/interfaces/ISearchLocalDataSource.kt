package com.application.real_estate_app.core_database.interfaces

interface ISearchLocalDataSource {

    suspend fun saveSearchQuery(query: String)

    suspend fun getSearchHistory(): List<String>

    suspend fun clearSearchHistory()
}