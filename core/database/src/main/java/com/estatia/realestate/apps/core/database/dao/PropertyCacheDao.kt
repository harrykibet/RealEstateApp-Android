package com.estatia.realestate.apps.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity

@Dao
interface PropertyCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(properties: List<PropertyCacheEntity>)

    @Query("SELECT * FROM properties_cache")
    suspend fun getAll(): List<PropertyCacheEntity>

    @Query("SELECT * FROM properties_cache WHERE id = :id")
    suspend fun getById(id: String): PropertyCacheEntity?

    @Query("DELETE FROM properties_cache")
    suspend fun clearAll()

    @Query("SELECT MAX(created_at) FROM properties_cache")
    suspend fun getLatestTimestamp(): Long?
}