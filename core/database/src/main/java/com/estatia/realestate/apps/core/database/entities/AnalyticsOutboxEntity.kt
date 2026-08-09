package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_outbox")
data class AnalyticsOutboxEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "event_json") val eventJson: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
