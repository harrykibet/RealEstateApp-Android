package com.estatia.realestate.apps.core.testing.generators

import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId
import java.util.UUID

/**
 * Utility for generating realistic domain models for testing.
 */
object PropertyGenerator {

    fun generateProperty(
        id: String = UUID.randomUUID().toString(),
        title: String = "Generated Property",
        price: Double = 50000.0
    ): PropertyDomainModel {
        return PropertyDomainModel(
            id = PropertyId(id),
            title = title,
            description = "Description for $id",
            price = Money(price),
            imageUrls = emptyList(),
            directVideoUrls = emptyList(),
            hlsUrls = emptyList(),
            videosAvailable = false,
            coordinates = Coordinates(0.0, 0.0),
            createdAt = System.currentTimeMillis(),
            ownerId = "owner_1",
            ownerName = "Owner One",
            contact = ContactInfo(null, null),
            county = "County",
            active = true,
            viewsCount = 0,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            matchScore = 0.5f,
            propertyType = "Apartment",
            bedrooms = 1,
            bathrooms = 1,
            areaSize = 50.0,
            amenities = emptyList(),
            features = null,
            depositAmount = null,
            address = "Address",
            availableFrom = null,
            leaseTerms = null,
            available = true
        )
    }
}
