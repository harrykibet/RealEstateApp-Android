package com.estatia.realestate.apps.core.network.mappers

import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel

object PropertyMapper {

    fun fromEntity(entity: PropertyEntityModel): PropertyDomainModel {
        return PropertyDomainModel(
            id = PropertyId(entity.id),
            title = entity.title.ifBlank { "Untitled" },

            description = entity.description,

            price = entity.price?.let { Money(it) },

            imageUrls = entity.imageUrl.orEmpty(),
            videoUrls = entity.videoUrl.orEmpty(),

            videosAvailable = entity.video,

            coordinates = if (entity.latitude != null && entity.longitude != null) {
                Coordinates(entity.latitude, entity.longitude)
            } else null,

            createdAt = entity.createdAt,

            ownerId = entity.ownerId,
            ownerName = entity.ownerName,

            contact = ContactInfo(
                phone = entity.contactPhone,
                email = entity.contactEmail
            ),

            county = entity.county,

            active = entity.active,

            viewsCount = entity.viewsCount,
            likesCount = entity.likesCount,
            commentsCount = entity.commentsCount,
            sharesCount = entity.sharesCount,

            propertyType = entity.propertyType,

            bedrooms = entity.bedrooms,
            bathrooms = entity.bathrooms,
            areaSize = entity.areaSize,

            amenities = entity.amenities.orEmpty(),

            features = entity.features,

            depositAmount = entity.depositAmount?.let { Money(it) },

            address = entity.address,
            availableFrom = entity.availableFrom,
            leaseTerms = entity.leaseTerms,

            available = entity.available
        )
    }
}