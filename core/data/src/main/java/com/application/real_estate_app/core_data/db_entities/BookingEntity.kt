package com.application.real_estate_app.core_data.db_entities

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

data class BookingEntity(
    val bookingId: String,
    val propertyId: String,
    val userId: String,
    val startDate: String,
    val endDate: String,
    val status: BookingStatus
)
