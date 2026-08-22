package com.estatia.realestate.apps.core.data.mappers.remote

import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel

/**
 * Maps remote comment entities to domain models and vice versa.
 * Used for both Firebase and AWS backends.
 */
internal object RemoteCommentMapper {

    // Map to Domain Model
    fun toDomain(comment: CommentEntityModel): CommentDomainModel {
        return CommentDomainModel(
            id = comment.id,
            authorId = comment.authorId,
            message = comment.message,
            timeStamp = comment.timeStamp,
            propertyId = comment.propertyId,
            authorName = comment.authorName
        )
    }

    // Map from Domain Model
    fun toEntity(comment: CommentDomainModel): CommentEntityModel {
        return CommentEntityModel(
            id = comment.id,
            authorId = comment.authorId,
            message = comment.message,
            timeStamp = comment.timeStamp,
            propertyId = comment.propertyId,
            authorName = comment.authorName
        )
    }
}
