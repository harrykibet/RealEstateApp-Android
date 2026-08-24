package com.estatia.realestate.apps.feature.search.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the property search experience.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage search queries, history, and result state.
 * - Concurrency: Thread-safe via [viewModelScope] and state [update] calls.
 * - Resilience: Handles remote search failures via [SearchUiState.Error].
 * - State Restoration: Persists current query [KEY_QUERY] and scroll page [KEY_SCROLL_PAGE].
 * - Performance: Avoids duplicate searches for the same query.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: ISearchRepository,
    private val engagementRepository: IEngagementRepository,
    private val togglePropertyLikeUseCase: TogglePropertyLikeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SCROLL_PAGE = "search_scroll_page"
        private const val KEY_QUERY = "search_query"
    }

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        val restoredQuery = savedStateHandle.get<String>(KEY_QUERY)
        if (!restoredQuery.isNullOrBlank()) {
            searchProperties(restoredQuery)
        } else {
            loadSearchHistory()
        }
    }

    fun onQueryChanged(query: String) {
        savedStateHandle[KEY_QUERY] = query
        if (query.isBlank()) {
            loadSearchHistory()
            return
        }
        searchProperties(query)
    }

    fun searchProperties(query: String) {
        if (query.isBlank()) return
        
        // 🛡️ Avoid redundant searches for the same active query
        val current = _uiState.value
        if (current is SearchUiState.Success && current.query == query) return

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            when (val result = searchRepository.searchProperties(query, 20)) {
                is AppResult.Success -> {
                    val savedPage = savedStateHandle.get<Int>(KEY_SCROLL_PAGE) ?: 0
                    _uiState.value = SearchUiState.Success(
                        results = result.data,
                        query = query,
                        initialPage = savedPage
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

    fun toggleLike(propertyId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            togglePropertyLikeUseCase(propertyId, isCurrentlyLiked)
        }
    }

    fun onPageChanged(page: Int) {
        savedStateHandle[KEY_SCROLL_PAGE] = page
        val current = _uiState.value
        if (current is SearchUiState.Success) {
            _uiState.value = current.copy(initialPage = page)
        }
    }

    fun onResultClicked(propertyId: String) {
        val current = _uiState.value
        if (current is SearchUiState.Success) {
            viewModelScope.launch {
                engagementRepository.reportSearch(current.query, propertyId)
            }
        }
    }
}
