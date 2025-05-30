package com.estatia.realestate.apps.core.database.interfaces

interface ISearchLocalDataSource {

    suspend fun saveSearchQuery(query: String)

    suspend fun getSearchHistory(): List<String>

    suspend fun clearSearchHistory()
}