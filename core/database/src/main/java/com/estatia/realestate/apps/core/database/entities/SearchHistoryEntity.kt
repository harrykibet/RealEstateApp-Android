package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

@Entity(tableName = "search_history")
@EntityModel
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") // Explicit column name
    val id: Int = 0, // Auto-increment ID for ordering

    @ColumnInfo(name = "query") // Explicit column name
    val query: String, // The search query string

    @ColumnInfo(name = "timestamp") // Explicit column name
    val timestamp: Long = System.currentTimeMillis() // Timestamp to manage recency
)
