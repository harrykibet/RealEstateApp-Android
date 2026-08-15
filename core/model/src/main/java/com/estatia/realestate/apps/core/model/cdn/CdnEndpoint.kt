package com.estatia.realestate.apps.core.model.cdn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CdnEndpoint(
    val name: String,
    @SerialName("base_url")
    val baseUrl: String
)
