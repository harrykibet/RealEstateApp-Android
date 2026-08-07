package com.estatia.realestate.apps.core.data.mappers.room

import com.estatia.realestate.apps.core.database.entities.CommentCacheEntity
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel

object RoomCommentMapper {

    fun toDomain(entity: CommentCacheEntity): CommentDomainModel {
        return CommentDomainModel(
            id = entity.id,
            authorId = entity.authorId,
            message = entity.message,
            timeStamp = entity.timestamp,
            propertyId = entity.propertyId,
            authorName = entity.authorName
        )
    }

    fun toEntity(domain: CommentDomainModel): CommentCacheEntity {
        return CommentCacheEntity(
            id = domain.id ?: "",
            propertyId = domain.propertyId,
            authorId = domain.authorId,
            authorName = domain.authorName,
            message = domain.message,
            timestamp = domain.timeStamp
        )
    }
}
