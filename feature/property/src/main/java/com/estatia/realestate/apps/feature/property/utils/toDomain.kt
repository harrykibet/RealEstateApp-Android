package com.estatia.realestate.apps.feature.property.utils

import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId
import java.util.UUID

fun AddPropertyDraft.toDomain(
    userId: String
): PropertyDomainModel {


    return PropertyDomainModel(

        id = PropertyId(
            value = UUID.randomUUID().toString()
        ),

        title = title,

        description = description,

        price = price?.let {
            Money(it)
        },

        imageUrls = emptyList(),

        directVideoUrls = emptyList(),

        hlsUrls = emptyList(),

        videosAvailable = videos.isNotEmpty(),


        coordinates =
            if(latitude != null && longitude != null)
                Coordinates(
                    latitude,
                    longitude
                )
            else null,


        createdAt = System.currentTimeMillis(),

        ownerId = userId,

        ownerName = null,


        contact = ContactInfo(
            email = contactEmail,
            phone = contactPhone
        ),


        county = county,


        active = true,


        viewsCount = 0,
        likesCount = 0,
        commentsCount = 0,
        sharesCount = 0,
        matchScore = 0.5f,

        propertyType = propertyType,


        bedrooms = bedrooms,
        bathrooms = bathrooms,
        areaSize = areaSize,


        amenities = amenities.toList(),


        features = features,


        depositAmount =
            depositAmount?.let {
                Money(it)
            },


        address = address,


        availableFrom = availableFrom,

        leaseTerms = leaseTerms,


        available = true
    )
}
