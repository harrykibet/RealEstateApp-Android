package com.estatia.realestate.apps.feature.property.utils

data class AddPropertyUiState(

    val countyNames: List<String> = emptyList(),

    val propertyTypes: List<String> = emptyList(),

    val isUploading: Boolean = false,

    val uploadError: String? = null
)
