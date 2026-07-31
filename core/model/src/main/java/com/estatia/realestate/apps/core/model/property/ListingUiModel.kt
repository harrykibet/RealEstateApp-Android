package com.estatia.realestate.apps.core.model.property

/**
 * A lightweight UI model for representing a property listing in a feed.
 */
data class ListingUiModel(
    val id: String,
    val title: String,
    val description: String?,
    val videoUrl: String?,
    val ownerName: String,
    val ownerAvatarUrl: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
)

fun PropertyDomainModel.toListingUiModel(): ListingUiModel =
    ListingUiModel(
        id = id.value,
        title = title,
        description = description,
        videoUrl = videoUrls.firstOrNull(),
        ownerName = ownerName ?: "Unknown",
        ownerAvatarUrl = null, // Placeholder as PropertyDomainModel doesn't have it yet
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
    )
