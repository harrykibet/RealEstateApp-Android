package com.estatia.realestate.apps.core.model.feature

import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
data class VideoItem(
    val mediaId: String,
    val title: String = "",
    val description: String = "",
    val thumbnailUrl: String = "",
    val mediaType: MediaType = MediaType.VOD,
    val directVideoUrl: String = ""
)
