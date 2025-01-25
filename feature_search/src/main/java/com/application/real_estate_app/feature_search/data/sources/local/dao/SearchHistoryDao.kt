package com.application.real_estate_app.feature_search.data.sources.local.dao

import androidx.room.*
import com.application.real_estate_app.feature_search.data.sources.local.entities.SearchHistoryEntity

@Dao
interface SearchHistoryDao {

    // Insert a search query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchHistoryEntity)

    // Fetch the last 10 search queries, ordered by most recent
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    suspend fun getSearchHistory(): List<SearchHistoryEntity>

    // Delete older entries to maintain only the last 10 searches
    @Query("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY timestamp DESC LIMIT 10)")
    suspend fun maintainSearchHistoryLimit()

    // Clear all search history
    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}
