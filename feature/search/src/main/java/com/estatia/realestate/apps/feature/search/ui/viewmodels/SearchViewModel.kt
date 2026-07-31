package com.estatia.realestate.apps.feature.search.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.data.interfaces.ISearchRepository
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: ISearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadSearchHistory()
    }

    fun onQueryChanged(query: String) {
        if (query.isBlank()) {
            loadSearchHistory()
            return
        }
        searchProperties(query)
    }

    fun searchProperties(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            when (val result = searchRepository.searchProperties(query, 20)) {
                is AppResult.Success -> {
                    _uiState.value = SearchUiState.Success(
                        results = result.data,
                        query = query,
                    )
                }
                is AppResult.Error -> {
                    _uiState.value = SearchUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            when (val result = searchRepository.getSearchHistory()) {
                is AppResult.Success -> {
                    _uiState.value = SearchUiState.History(result.data)
                }
                is AppResult.Error -> {
                    _uiState.value = SearchUiState.History(emptyList())
                }
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchRepository.clearSearchHistory()
            _uiState.value = SearchUiState.History(emptyList())
        }
    }
}
