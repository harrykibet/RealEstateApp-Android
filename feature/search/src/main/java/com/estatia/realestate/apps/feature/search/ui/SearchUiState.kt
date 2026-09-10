package com.estatia.realestate.apps.feature.search.ui

import com.estatia.realestate.apps.core.architecture.annotations.Helper

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

sealed interface SearchUiState {
    val initialPage: Int
        get() = 0

@Helper
    object Initial : SearchUiState
@Helper
    object Loading : SearchUiState
    data class Success(
        val results: List<PropertyDomainModel>,
        val query: String,
        override val initialPage: Int = 0
    ) : SearchUiState
    data class History(
        val history: List<String>
    ) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
