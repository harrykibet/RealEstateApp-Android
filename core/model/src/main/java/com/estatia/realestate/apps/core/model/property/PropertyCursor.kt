package com.estatia.realestate.apps.core.model.property


data class PropertyCursor(
    val createdAt: Long,
    val documentId: String
)


data class PropertyPage(
    val properties: List<PropertyDomainModel>,
    val cursor: PropertyCursor?
)
