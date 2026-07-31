package com.estatia.realestate.apps.feature.search.ui

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel

sealed interface SearchUiState {
    object Initial : SearchUiState
    object Loading : SearchUiState
    data class Success(
        val results: List<PropertyDomainModel>,
        val query: String
    ) : SearchUiState
    data class History(
        val history: List<String>
    ) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
