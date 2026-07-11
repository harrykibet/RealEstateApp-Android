package com.estatia.realestate.apps.core.data.mappers

import com.estatia.realestate.apps.core.database.entities.PropertyCacheEntity
import com.estatia.realestate.apps.core.model.property.ContactInfo
import com.estatia.realestate.apps.core.model.property.Coordinates
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.PropertyId
import com.google.gson.Gson
import kotlin.collections.toList

object LocalDbPropertyMapper {

    private val gson = Gson()

    private fun List<String>.toJson(): String = gson.toJson(this)

    private fun String.toList(): List<String> =
        try {
            gson.fromJson(this, Array<String>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

    fun toEntity(domain: PropertyDomainModel): PropertyCacheEntity {
        return PropertyCacheEntity(
            id = domain.id.value,

            title = domain.title,
            description = domain.description,
            price = domain.price?.amount,

            imageUrls = domain.imageUrls.toJson(),
            videoUrls = domain.videoUrls.toJson(),

            videosAvailable = domain.videosAvailable,

            latitude = domain.coordinates?.latitude,
            longitude = domain.coordinates?.longitude,

            createdAt = domain.createdAt,

            ownerId = domain.ownerId,
            ownerName = domain.ownerName,

            contactPhone = domain.contact.phone,
            contactEmail = domain.contact.email,

            county = domain.county,

            active = domain.active,

            viewsCount = domain.viewsCount,
            likesCount = domain.likesCount,
            commentsCount = domain.commentsCount,
            sharesCount = domain.sharesCount
        )
    }

    fun toDomain(entity: PropertyCacheEntity): PropertyDomainModel {
        return PropertyDomainModel(
            id = PropertyId(entity.id),

            title = entity.title,

            description = entity.description,

            price = entity.price?.let {
                Money(it)
            },

            imageUrls = entity.imageUrls.toList(),
            videoUrls = entity.videoUrls.toList(),

            videosAvailable = entity.videosAvailable,

            coordinates = if (entity.latitude != null && entity.longitude != null) {
                Coordinates(
                    entity.latitude!!,
                    entity.longitude!!
                )
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

            propertyType = null, // cache can omit optional enrichment fields

            bedrooms = null,
            bathrooms = null,
            areaSize = null,

            amenities = emptyList(),

            features = null,

            depositAmount = null,

            address = null,
            availableFrom = null,
            leaseTerms = null,

            available = entity.active
        )
    }

    fun List<PropertyDomainModel>.toCacheEntities() =
        map(LocalDbPropertyMapper::toEntity)
}