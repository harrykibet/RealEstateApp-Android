package com.estatia.realestate.apps.core.data.mappers

import com.estatia.realestate.apps.core.model.feature.Comment
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel

object RemoteCommentMapper {

    // Map to Domain Model
    fun toDomain(comment: CommentEntityModel): Comment {
        return Comment(
            id = comment.id,
            authorId = comment.authorId,
            message = comment.message,
            timeStamp = comment.timeStamp,
            propertyId = comment.propertyId,
            authorName = comment.authorName
        )
    }

    // Map from Domain Model
    fun toEntity(comment: Comment): CommentEntityModel {
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
