package com.estatia.realestate.apps.feature.property.ui.management.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyDetailsViewModel @Inject constructor(
    private val repository: IPropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PropertyDetailsUiState>(PropertyDetailsUiState.Loading)
    val uiState: StateFlow<PropertyDetailsUiState> = _uiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            _uiState.value = PropertyDetailsUiState.Loading
            when (val result = repository.getPropertyById(id)) {
                is AppResult.Success -> {
                    _uiState.value = PropertyDetailsUiState.Success(result.data)
                }
                is AppResult.Error -> {
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
