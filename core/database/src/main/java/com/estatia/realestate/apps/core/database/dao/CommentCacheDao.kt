package com.estatia.realestate.apps.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.estatia.realestate.apps.core.database.entities.CommentCacheEntity

@Dao
interface CommentCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentCacheEntity>)

    @Query("SELECT * FROM comments_cache WHERE property_id = :propertyId ORDER BY timestamp DESC")
    suspend fun getForProperty(propertyId: String): List<CommentCacheEntity>

    @Query("DELETE FROM comments_cache WHERE property_id = :propertyId")
    suspend fun clearForProperty(propertyId: String)

    @Query("SELECT COUNT(*) FROM comments_cache")
    suspend fun count(): Int

    @Query("DELETE FROM comments_cache WHERE id NOT IN (SELECT id FROM comments_cache ORDER BY timestamp DESC LIMIT :targetSize)")
    suspend fun trim(targetSize: Int)
}
