package com.estatia.realestate.apps.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property_drafts")
data class PropertyDraftEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "draft_id")
    val draftId: Int = 0, // Auto-generated ID for drafts

    @ColumnInfo(name = "title")
    val title: String?, // Property title

    @ColumnInfo(name = "description")
    val description: String?, // Description of the property

    @ColumnInfo(name = "price")
    val price: Double?, // Property price

    @ColumnInfo(name = "address")
    val address: String?, // Address of the property

    @ColumnInfo(name = "property_type")
    val propertyType: String?, // Type of property (e.g., Apartment, House)

    @ColumnInfo(name = "bedrooms")
    val bedrooms: Int?, // Number of bedrooms

    @ColumnInfo(name = "bathrooms")
    val bathrooms: Int?, // Number of bathrooms

    @ColumnInfo(name = "area_size")
    val areaSize: Double?, // Size of the property in square meters

    @ColumnInfo(name = "deposit_amount")
    val depositAmount: Double?, // Deposit amount for the property

    @ColumnInfo(name = "available_from")
    val availableFrom: String?, // Date the property becomes available

    @ColumnInfo(name = "lease_terms")
    val leaseTerms: String?, // Lease terms for the property

    @ColumnInfo(name = "amenities")
    val amenities: List<String>?, // List of amenities

    @ColumnInfo(name = "features")
    val features: String?, // Additional features of the property

    @ColumnInfo(name = "latitude")
    val latitude: Double?, // Latitude for geolocation

    @ColumnInfo(name = "longitude")
    val longitude: Double?, // Longitude for geolocation

    @ColumnInfo(name = "image_urls")
    val imageUrls: List<String>?, // List of local URIs for property images

    @ColumnInfo(name = "video_urls")
    val videoUrls: List<String>? // List of local URIs for property videos
)
