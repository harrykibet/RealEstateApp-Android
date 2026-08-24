package com.estatia.realestate.apps.feature.home.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.getOrThrow
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.feature.home.ui.HomeUiState
import kotlin.time.Duration.Companion.milliseconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home Feed.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the infinite scrolling property feed.
 * - Concurrency: Thread-safe via [viewModelScope] and state [update] calls.
 * - Resilience: Implements pagination and manual retry on network failure.
 * - Observability: Tracks feed latency and pagination funnel.
 * - State Restoration: Persists current scroll position [KEY_SCROLL_PAGE].
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: IPropertyRepository,
    private val authRepository: IAuthRepository,
    private val togglePropertyLikeUseCase: TogglePropertyLikeUseCase,
    private val metricsTracker: IMetricsTracker,
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
            val startTime = System.currentTimeMillis()
            try {
                val userId = authRepository.getCurrentUserId()
                val result = api.fetchPropertiesPaginated(userId, cursor, pageSize)
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
                
                metricsTracker.incrementCounter("home.feed.page_load.success")
            } catch (_: Exception) {
                metricsTracker.incrementCounter("home.feed.page_load.failure")
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to fetch properties. Please try again.")
                }
            } finally {
                metricsTracker.trackDuration("home.feed.latency", (System.currentTimeMillis() - startTime).milliseconds)
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
