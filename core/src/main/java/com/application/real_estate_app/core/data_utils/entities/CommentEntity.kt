package com.application.real_estate_app.core.data_utils.entities

import com.application.real_estate_app.domain.models.Comment
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CommentEntity(
    val id: String? = null,
    val userId: String? = null,
    val commentText: String? = null,
    @ServerTimestamp val timeStamp: Date? = null
) {
    // Map to Domain Model
    fun toDomainModel() = Comment(
        id = id,
        userId = userId,
        commentText = commentText,
        timeStamp = timeStamp
    )

    companion object {
        // Map from Domain Model
        fun fromDomainModel(comment: Comment) = CommentEntity(
            id = comment.id,
            userId = comment.userId,
            commentText = comment.commentText,
            timeStamp = comment.timeStamp
        )
    }
}
