package com.estatia.realestate.apps.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.estatia.realestate.apps.core.database.entities.AnalyticsOutboxEntity

@Dao
interface AnalyticsOutboxDao {
    @Insert
    suspend fun insert(event: AnalyticsOutboxEntity)

    @Query("SELECT * FROM analytics_outbox ORDER BY created_at ASC")
    suspend fun getAll(): List<AnalyticsOutboxEntity>

    @Delete
    suspend fun delete(event: AnalyticsOutboxEntity)

    @Query("DELETE FROM analytics_outbox")
    suspend fun clear()
}
