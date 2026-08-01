package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties_drafts")
data class PropertyDraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "price")
    val price: Double?,

    @ColumnInfo(name = "image_urls")
    val imageUrls: String,
    @ColumnInfo(name = "video_urls")
    val videoUrls: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
