package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.estatia.realestate.apps.core.database.converters.RoomTypeConverters

@Entity(tableName = "search_results_cache")
@TypeConverters(RoomTypeConverters::class)
data class SearchCacheEntity(
    @PrimaryKey val query: String,
    @ColumnInfo(name = "property_ids")
    val propertyIds: List<String>,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
