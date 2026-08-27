package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId
import java.util.UUID

/**
 * Unified source of truth for property domain fixtures.
 * 
 * 🏗️ USAGE PATTERN:
 * - Deterministic: Use [default] for stable, rich values in snapshot/logic tests.
 * - Customized: Use [build] or [default].copy(...) for specific scenario testing.
 * - Randomized: Use [build] with default arguments for property-based testing.
 */
object PropertyFixtures : FixtureContract<PropertyDomainModel> {

    /**
     * Returns a rich, realistic property model with deterministic values.
     */
    override fun default(): PropertyDomainModel {
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

    /**
     * Factory method for building customized or randomized property models.
     */
    override fun build(id: String): PropertyDomainModel = build(id = id, title = "Generated Property")

    /**
     * Overload for [build] to support custom attributes.
     */
    fun build(
        id: String = UUID.randomUUID().toString(),
        title: String = "Generated Property",
        price: Double = 50000.0,
        ownerId: String = "owner_1"
    ): PropertyDomainModel {
        return default().copy(
            id = PropertyId(id),
            title = title,
            price = Money(price),
            ownerId = ownerId,
            createdAt = System.currentTimeMillis()
        )
    }

    override fun list(count: Int): List<PropertyDomainModel> = List(count) {
        build(
            id = "prop_${it + 1}",
            title = "Property #${it + 1}",
            price = 30000.0 + (it * 5000)
        ).copy(
            bedrooms = 1 + (it % 3),
            bathrooms = 1 + (it % 2),
            likesCount = 10 * (it + 1),
            viewsCount = 50 * (it + 1),
            imageUrls = listOf(
                "https://via.placeholder.com/600x400.png?text=Image+${it + 1}"
            )
        )
    }

    @Deprecated("Use default()", ReplaceWith("default()"))
    fun single(): PropertyDomainModel = default()
}
