package com.estatia.realestate.apps.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.estatia.realestate.apps.core.database.entities.SearchCacheEntity

@Dao
interface SearchCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchResult: SearchCacheEntity)

    @Query("SELECT * FROM search_results_cache WHERE `query` = :query")
    suspend fun get(query: String): SearchCacheEntity?

    @Query("DELETE FROM search_results_cache WHERE timestamp < :expiryTime")
    suspend fun clearExpired(expiryTime: Long)

    @Query("SELECT COUNT(*) FROM search_results_cache")
    suspend fun count(): Int

    @Query("DELETE FROM search_results_cache WHERE `query` NOT IN (SELECT `query` FROM search_results_cache ORDER BY timestamp DESC LIMIT :targetSize)")
    suspend fun trim(targetSize: Int)
}
