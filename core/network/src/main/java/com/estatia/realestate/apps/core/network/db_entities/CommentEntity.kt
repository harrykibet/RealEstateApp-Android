package com.estatia.realestate.apps.core.network.db_entities

import com.estatia.realestate.apps.core.model.feature.Comment
import com.google.firebase.firestore.ServerTimestamp

data class CommentEntity(
    val id: String? = null,
    val authorId: String,
    val message: String,
    val propertyId: String,
    val authorName: String,
    @ServerTimestamp val timeStamp: Long
) {
    // Map to Domain Model
    fun toDomainModel() = Comment(
        id = id,
        authorId = authorId,
        message = message,
        timestamp = timeStamp,
        propertyId = propertyId,
        authorName = authorName
    )

    companion object {
        // Map from Domain Model
        fun fromDomainModel(comment: Comment) = CommentEntity(
            id = comment.id,
            authorId = comment.authorId,
            message = comment.message,
            timeStamp = comment.timestamp,
            propertyId = comment.propertyId,
            authorName = comment.authorName
        )
    }
}
