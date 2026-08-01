package com.estatia.realestate.apps.core.data.mappers.firestore

import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel

object FirestoreCommentMapper {

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
