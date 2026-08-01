package com.estatia.realestate.apps.core.model.property

data class PropertyDraftDomainModel(
    val id: Long,

    val title: String?,
    val description: String?,
    val price: Double?,

    val imageUrls: List<String>,
    val videoUrls: List<String>,

    val createdAt: Long
)
