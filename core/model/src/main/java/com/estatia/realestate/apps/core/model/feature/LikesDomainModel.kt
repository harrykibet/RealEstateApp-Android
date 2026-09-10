package com.estatia.realestate.apps.core.model.feature
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
data class LikesDomainModel(
    val userId: String?,
    val likedAt: Long
)
