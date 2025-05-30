package com.estatia.realestate.apps.core.network.db_entities

import com.estatia.realestate.apps.core.model.feature.Likes
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
