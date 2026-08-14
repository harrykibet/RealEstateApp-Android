package com.estatia.realestate.apps.core.testing.data

import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId

object MockProperties {

    fun single(): PropertyDomainModel {
        return PropertyDomainModel(
            id = PropertyId("prop_001"),
            title = "Modern 2 Bedroom Apartment",
            description = "A spacious and modern apartment in the heart of the city with all amenities included.",
            price = Money(55000.0),
            imageUrls = listOf(
                "https://via.placeholder.com/600x400.png?text=Living+Room",
                "https://via.placeholder.com/600x400.png?text=Bedroom",
                "https://via.placeholder.com/600x400.png?text=Kitchen"
            ),
            directVideoUrls = listOf(
                "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4"
            ),
            hlsUrls = emptyList(),
            videosAvailable = true,
            coordinates = Coordinates(latitude = -1.2921, longitude = 36.8219),
            createdAt = 1_746_619_200_000,
            ownerId = "user_123",
            ownerName = "James Kibet",
            contact = ContactInfo(
                phone = "+254712345678",
                email = "owner@example.com",
            ),
            county = "Nairobi",
            active = true,
            viewsCount = 152,
            likesCount = 45,
            commentsCount = 12,
            sharesCount = 6,
            matchScore = 0.95f,
            propertyType = "Apartment",
            bedrooms = 2,
            bathrooms = 2,
            areaSize = 85.0,
            amenities = listOf("WiFi", "Parking", "Swimming Pool"),
            features = "Balcony, Security System, Backup Generator",
            depositAmount = Money(55000.0),
            address = "Westlands, Nairobi",
            availableFrom = "2025-06-01",
            leaseTerms = "12 months minimum lease",
            available = true
        )
    }

    fun list(count: Int = 5): List<PropertyDomainModel> = List(count) {
        single().copy(
            id = PropertyId("prop_${it + 1}"),
            title = "Property #${it + 1}",
            price = Money(30000.0 + (it * 5000)),
            bedrooms = 1 + (it % 3),
            bathrooms = 1 + (it % 2),
            likesCount = 10 * (it + 1),
            viewsCount = 50 * (it + 1),
            imageUrls = listOf(
                "https://via.placeholder.com/600x400.png?text=Image+${it + 1}"
            )
        )
    }
}
