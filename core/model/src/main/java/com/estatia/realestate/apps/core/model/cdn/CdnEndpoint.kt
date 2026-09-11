package com.estatia.realestate.apps.core.model.cdn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@Serializable
@DomainModel
data class CdnEndpoint(
    val name: String,
    @SerialName("base_url")
    val baseUrl: String
)
