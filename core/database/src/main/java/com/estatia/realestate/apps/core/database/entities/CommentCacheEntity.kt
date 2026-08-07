package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments_cache")
data class CommentCacheEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "property_id")
    val propertyId: String,
    @ColumnInfo(name = "author_id")
    val authorId: String,
    @ColumnInfo(name = "author_name")
    val authorName: String,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
