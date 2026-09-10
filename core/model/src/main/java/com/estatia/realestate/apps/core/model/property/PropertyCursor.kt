package com.estatia.realestate.apps.core.model.property
import com.estatia.realestate.apps.core.architecture.annotations.DomainModel


@DomainModel
data class PropertyCursor(
    val createdAt: Long,
    val documentId: String
)


@DomainModel
data class PropertyPage(
    val properties: List<PropertyDomainModel>,
    val cursor: PropertyCursor?
)
