package com.estatia.realestate.apps.core.network.db_entities
import com.estatia.realestate.apps.core.architecture.annotations.Data.EntityModel

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

@EntityModel
data class BookingEntity(
    val bookingId: String,
    val propertyId: String,
    val userId: String,
    val startDate: String,
    val endDate: String,
    val status: BookingStatus
)
