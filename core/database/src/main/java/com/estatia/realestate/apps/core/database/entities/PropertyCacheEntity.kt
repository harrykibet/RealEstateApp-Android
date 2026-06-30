package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties_cache")
data class PropertyCacheEntity(
    @PrimaryKey val id: String,

    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "price")
    val price: Double?,

    @ColumnInfo(name = "image_urls")
    val imageUrls: String, // stored as JSON string
    @ColumnInfo(name = "video_urls")
    val videoUrls: String,

    @ColumnInfo(name = "videos_available")
    val videosAvailable: Boolean,

    @ColumnInfo(name = "latitude")
    val latitude: Double?,
    @ColumnInfo(name = "longitude")
    val longitude: Double?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long?,

    @ColumnInfo(name = "owner_id")
    val ownerId: String?,
    @ColumnInfo(name = "owner_name")
    val ownerName: String?,

    @ColumnInfo(name = "contact_phone")
    val contactPhone: String?,
    @ColumnInfo(name = "contact_email")
    val contactEmail: String?,

    @ColumnInfo(name = "county")
    val county: String?,

    @ColumnInfo(name = "active")
    val active: Boolean,


    @ColumnInfo(name = "views_count")
    val viewsCount: Int,
    @ColumnInfo(name = "likes_count")
    val likesCount: Int,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int,
    @ColumnInfo(name = "shares_count")
    val sharesCount: Int
)