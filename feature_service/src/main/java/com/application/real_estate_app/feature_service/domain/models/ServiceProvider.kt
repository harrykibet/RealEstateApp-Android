package com.application.real_estate_app.feature_service.domain.models

import com.application.real_estate_app.feature_service.data.utils.ServiceType
import com.application.real_estate_app.core.domain.models.Location
import java.util.Date

data class ServiceProvider(
    val id: String,
    val name: String,
    val serviceType: ServiceType,
    val description: String?,
    val contactPhone: String,
    val contactEmail: String?,
    val websiteUrl: String?,
    val location: Location?,
    val serviceAreas: List<String>,
    val ratings: Float?,
    val reviewsCount: Int,
    val verified: Boolean,
    val images: List<String>,
    val createdAt: Date?
)
