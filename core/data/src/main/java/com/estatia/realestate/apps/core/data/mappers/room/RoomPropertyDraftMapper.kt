package com.estatia.realestate.apps.core.data.mappers.room

import com.estatia.realestate.apps.core.database.entities.PropertyDraftEntity
import com.estatia.realestate.apps.core.model.property.PropertyDraftDomainModel

internal object RoomPropertyDraftMapper {

    fun toDomain(
        entity: PropertyDraftEntity
    ): PropertyDraftDomainModel {

        return PropertyDraftDomainModel(

            id = entity.id,

            title = entity.title,

            description = entity.description,

            price = entity.price,

            imageUrls = JsonConverter.fromJson(entity.imageUrls),

            directVideoUrls = JsonConverter.fromJson(entity.directVideoUrls),

            createdAt = entity.createdAt
        )
    }

    fun toEntity(
        domain: PropertyDraftDomainModel
    ): PropertyDraftEntity {
        return PropertyDraftEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            price = domain.price,
            imageUrls = JsonConverter.toJson(domain.imageUrls),
            directVideoUrls = JsonConverter.toJson(domain.directVideoUrls),
            createdAt = domain.createdAt
        )
    }


    fun List<PropertyDraftEntity>.toDomainModels() =
        map(::toDomain)
}
