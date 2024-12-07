package com.application.real_estate_app.data.models

import com.application.real_estate_app.domain.models.Likes
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LikesEntity(
    val userId: String = "",
    @ServerTimestamp val likedAt: Date? = null
) {
    // Map to Domain Model
    fun toDomainModel() = Likes(
        userId = userId,
        likedAt = likedAt
    )

    companion object {
        // Map from Domain Model
        fun fromDomainModel(likes: Likes) = LikesEntity(
            userId = likes.userId,
            likedAt = likes.likedAt
        )
    }
}
