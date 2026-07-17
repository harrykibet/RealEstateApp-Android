package com.estatia.realestate.apps.core.network.db_entities

data class PropertyCursor(
    val createdAt: Long,
    val documentId: String
)


data class PropertyPage(
    val properties: List<PropertyEntityModel>,
    val cursor: PropertyCursor?
)