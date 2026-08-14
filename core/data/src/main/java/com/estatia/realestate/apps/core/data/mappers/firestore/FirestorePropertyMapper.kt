package com.estatia.realestate.apps.core.data.mappers.firestore

import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel

internal object FirestorePropertyMapper {

    fun toDomain(entity: PropertyEntityModel): PropertyDomainModel {
        return PropertyDomainModel(
            id = PropertyId(entity.id),
            title = entity.title.ifBlank { "Untitled" },

            description = entity.description,

            price = entity.price?.let { Money(it) },

            imageUrls = entity.imageUrl.orEmpty(),
            videoUrls = entity.videoUrl.orEmpty(),
            hlsUrls = entity.hlsUrl.orEmpty(),

            videosAvailable = entity.video,

            coordinates = entity.latitude?.let { lat ->
                entity.longitude?.let { lng ->
                    Coordinates(lat, lng)
                }
            },

            createdAt = entity.createdAt,

            ownerId = entity.ownerId,
            ownerName = entity.ownerName,

            contact = ContactInfo(
                phone = entity.contact?.phone,
                email = entity.contact?.email
            ),

            county = entity.county,

            active = entity.active,

            viewsCount = entity.viewsCount,
            likesCount = entity.likesCount,
            commentsCount = entity.commentsCount,
            sharesCount = entity.sharesCount,

            matchScore = entity.matchScore,

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

    fun toEntity(domain: PropertyDomainModel): PropertyEntityModel {
        return PropertyEntityModel(
            id = domain.id.value,
            title = domain.title,
            description = domain.description,
            price = domain.price?.amount,
            imageUrl = domain.imageUrls,
            videoUrl = domain.videoUrls,
            hlsUrl = domain.hlsUrls,
            video = domain.videosAvailable,
            latitude = domain.coordinates?.latitude,
            longitude = domain.coordinates?.longitude,
            createdAt = domain.createdAt,
            ownerId = domain.ownerId,
            ownerName = domain.ownerName,
            contact = null, // Contact info is uploaded separately to a subcollection
            county = domain.county,
            active = domain.active,
            viewsCount = domain.viewsCount,
            likesCount = domain.likesCount,
            commentsCount = domain.commentsCount,
            sharesCount = domain.sharesCount,
            matchScore = domain.matchScore,
            propertyType = domain.propertyType,
            bedrooms = domain.bedrooms,
            bathrooms = domain.bathrooms,
            areaSize = domain.areaSize,
            amenities = domain.amenities,
            features = domain.features,
            depositAmount = domain.depositAmount?.amount,
            address = domain.address,
            availableFrom = domain.availableFrom,
            leaseTerms = domain.leaseTerms,
            available = domain.available
        )
    }

    fun PropertyEntityModel?.toDomainOrNull(): PropertyDomainModel? =
        this?.let(FirestorePropertyMapper::toDomain)

    fun List<PropertyEntityModel?>.toDomainModels(): List<PropertyDomainModel> =
        mapNotNull { it.toDomainOrNull() }
}
