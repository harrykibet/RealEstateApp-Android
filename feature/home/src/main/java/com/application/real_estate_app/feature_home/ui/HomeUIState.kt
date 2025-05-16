package com.application.real_estate_app.feature_home.ui

import com.application.real_estate_app.core_model.property.Property

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val properties: List<Property> = emptyList()
)
