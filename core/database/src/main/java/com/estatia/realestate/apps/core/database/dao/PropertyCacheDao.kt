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

    @Query("SELECT * FROM properties_cache WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<PropertyCacheEntity>

    @Query("DELETE FROM properties_cache")
    suspend fun clearAll()

    @Query("SELECT MAX(created_at) FROM properties_cache")
    suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM properties_cache")
    suspend fun count(): Int

    @Query("DELETE FROM properties_cache WHERE id NOT IN (SELECT id FROM properties_cache ORDER BY is_liked DESC, created_at DESC LIMIT :targetSize)")
    suspend fun trim(targetSize: Int)
}
