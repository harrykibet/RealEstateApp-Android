package com.estatia.realestate.apps.core.network.mappers

import com.estatia.realestate.apps.core.network.db_entities.PropertyEntity
import com.estatia.realestate.apps.core.model.property.Property

// Extension function to convert PropertyEntity to Domain model
fun PropertyEntity.toDomainModel(): Property {
    return Property(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrls = imageUrl,
        videoUrls = videoUrl,
        videosAvailable = video,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        ownerId = ownerId,
        ownerName = ownerName,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        county = county,
        active = active,
        viewsCount = viewsCount,
        sharesCount = sharesCount,
        likesCount = likesCount,
        commentsCount = commentsCount,
        propertyType = propertyType,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        areaSize = areaSize,
        amenities = amenities,
        features = features,
        depositAmount = depositAmount,
        address = address,
        availableFrom = availableFrom,
        leaseTerms = leaseTerms,
        available = available
    )
}

// Extension function to convert Domain model to PropertyEntity
fun Property.toEntityModel(): PropertyEntity {
    return PropertyEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        imageUrl = imageUrls,
        videoUrl = videoUrls,
        video = videosAvailable,
        latitude = latitude,
        longitude = longitude,
        createdAt = createdAt,
        ownerId = ownerId,
        ownerName = ownerName,
        contactPhone = contactPhone,
        contactEmail = contactEmail,
        county = county,
        active = active,
        viewsCount = viewsCount,
        sharesCount = sharesCount,
        likesCount = likesCount,
        commentsCount = commentsCount,
        propertyType = propertyType,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        areaSize = areaSize,
        amenities = amenities,
        features = features,
        depositAmount = depositAmount,
        address = address,
        availableFrom = availableFrom,
        leaseTerms = leaseTerms,
        available = available
    )
}