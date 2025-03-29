package com.application.real_estate_app.core_network.db_entities

import com.application.real_estate_app.core_model.Likes
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LikesEntity(
    val userId: String? = null,
    @ServerTimestamp val likedAt: Date? = null
) {
    // Map to Domain Model
    @Suppress("unused")
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
