package com.estatia.realestate.apps.core.network.db_entities

import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

@EntityModel
data class PropertyRemotePage(
    val properties: List<PropertyEntityModel>,
    val cursor: PropertyCursor?
)
