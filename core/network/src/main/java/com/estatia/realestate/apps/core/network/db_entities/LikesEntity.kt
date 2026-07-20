package com.estatia.realestate.apps.core.network.db_entities

import com.estatia.realestate.apps.core.model.feature.LikesDomainModel
import com.google.firebase.firestore.ServerTimestamp

data class LikesEntity(
    val userId: String? = null,
    @ServerTimestamp val likedAt: Long
) {
    // Map to Domain Model
    @Suppress("unused")
    fun toDomainModel() = LikesDomainModel(
        userId = userId,
        likedAt = likedAt
    )

    companion object {
        // Map from Domain Model
        fun fromDomainModel(likes: LikesDomainModel) = LikesEntity(
            userId = likes.userId,
            likedAt = likes.likedAt
        )
    }
}
