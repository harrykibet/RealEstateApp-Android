package com.estatia.realestate.apps.feature.property.ui.management.viewmodels

import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for displaying the detailed view of a specific property.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the retrieval and display state of a property entity.
 * - Concurrency: Thread-safe via [viewModelScope].
 * - Resilience: Surfaces error state if retrieval fails.
 * - Observability: Tracks property detail load success and failure rates.
 */
@ViewModelMarker
@HiltViewModel
class PropertyDetailsViewModel @Inject constructor(
    private val repository: IPropertyRepository,
    private val metricsTracker: IMetricsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<PropertyDetailsUiState>(PropertyDetailsUiState.Loading)
    val uiState: StateFlow<PropertyDetailsUiState> = _uiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            _uiState.value = PropertyDetailsUiState.Loading
            when (val result = repository.getPropertyById(id)) {
                is AppResult.Success -> {
                    metricsTracker.incrementCounter("property.details.load.success")
                    _uiState.value = PropertyDetailsUiState.Success(result.data)
                }
                is AppResult.Error -> {
                    metricsTracker.incrementCounter("property.details.load.failure")
                    _uiState.value = PropertyDetailsUiState.Error(result.exception.message ?: "Failed to load property")
                }
            }
        }
    }
}

sealed interface PropertyDetailsUiState {
    object Loading : PropertyDetailsUiState
    data class Success(val property: PropertyDomainModel) : PropertyDetailsUiState
    data class Error(val message: String) : PropertyDetailsUiState
}
