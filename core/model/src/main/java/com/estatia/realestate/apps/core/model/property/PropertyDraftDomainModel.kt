package com.estatia.realestate.apps.core.model.property
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
data class PropertyDraftDomainModel(
    val id: Long,

    val title: String?,
    val description: String?,
    val price: Double?,

    val imageUrls: List<String>,
    val directVideoUrls: List<String>,

    val createdAt: Long
)
