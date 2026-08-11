package com.estatia.realestate.apps.feature.home.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.getOrThrow
import com.estatia.realestate.apps.core.domain.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.feature.home.ui.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: IPropertyRepository,
    private val togglePropertyLikeUseCase: TogglePropertyLikeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SCROLL_PAGE = "home_scroll_page"
    }

    private val _uiState = MutableStateFlow(HomeUiState(
        initialPage = savedStateHandle.get<Int>(KEY_SCROLL_PAGE) ?: 0
    ))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var cursor: PropertyCursor? = null
    private var canLoadMore = true

    init {
        fetchProperties(isFirstLoad = true, pageSize = 20)
    }

    fun fetchProperties(isFirstLoad: Boolean, pageSize: Int) {
        if (isFirstLoad) {
            cursor = null
            canLoadMore = true
        }

        if (!canLoadMore || _uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = api.fetchPropertiesPaginated(cursor, pageSize)
                val page = result.getOrThrow()
                
                val newProperties = page.properties
                cursor = page.cursor

                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        properties = if (isFirstLoad) newProperties else current.properties + newProperties
                    )
                }

                canLoadMore = newProperties.size == pageSize
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to fetch properties. Please try again.")
                }
            }
        }
    }

    fun canLoadMore(): Boolean = canLoadMore

    fun toggleLike(propertyId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            togglePropertyLikeUseCase(propertyId, isCurrentlyLiked)
            // Ideally, we should update the UI state locally here or refresh the properties
        }
    }

    fun onPageChanged(page: Int) {
        savedStateHandle[KEY_SCROLL_PAGE] = page
        _uiState.update { it.copy(initialPage = page) }
    }
}
