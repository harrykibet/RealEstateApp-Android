package com.estatia.realestate.apps.feature.search.ui

import com.estatia.realestate.apps.core.architecture.annotations.Data.UiState

import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface SearchUiState {
    val initialPage: Int
        get() = 0

@UiState
    object Initial : SearchUiState
@UiState
    object Loading : SearchUiState
@UiState
    data class Success(
        val results: List<PropertyDomainModel>,
        val query: String,
        override val initialPage: Int = 0
    ) : SearchUiState
@UiState
    data class History(
        val history: List<String>
    ) : SearchUiState
@UiState
    data class Error(val message: String) : SearchUiState
}
