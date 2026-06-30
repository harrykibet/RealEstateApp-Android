package com.estatia.realestate.apps.feature.home.ui

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val properties: List<PropertyDomainModel> = emptyList()
)
