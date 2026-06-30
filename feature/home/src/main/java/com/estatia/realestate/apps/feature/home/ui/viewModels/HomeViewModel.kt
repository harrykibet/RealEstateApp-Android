package com.estatia.realestate.apps.feature.home.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.feature.home.ui.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: IPropertyRepository,
    val exoPlayer: IPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _propertiesLiveData = MutableLiveData<List<PropertyDomainModel>>()
    val propertiesLiveData: LiveData<List<PropertyDomainModel>> get() = _propertiesLiveData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val isLoadingMutable = MutableLiveData<Boolean>()
    //val isLoading: LiveData<Boolean> get() = isLoadingMutable

    private var lastVisibleDocument: String? = null
    private var canLoadMore = true

    // Fetch properties with api in a coroutine
    fun fetchProperties(isFirstLoad: Boolean, pageSize: Int, onFailure: (Exception) -> Unit) {
        if (!canLoadMore || isLoadingMutable.value == true) return // Prevent redundant fetches

        isLoadingMutable.value = true

        viewModelScope.launch {
            try {
                val result = api.fetchPropertiesPaginated(lastVisibleDocument, pageSize, onFailure)
                val newProperties = result.first
                val lastVisible = result.second

                handleFetchedProperties(newProperties, isFirstLoad)

                // Update pagination state
                lastVisibleDocument = lastVisible
                canLoadMore = newProperties.size == pageSize

            } catch (exception: Exception) {
                // Error handling
                _errorMessage.value = "Failed to fetch properties. Please try again."
            } finally {
                isLoadingMutable.value = false
            }
        }
    }

    // Handle adding new properties to the list
    private fun handleFetchedProperties(newProperties: List<PropertyDomainModel>, isFirstLoad: Boolean) {
        if (isFirstLoad) {
            _propertiesLiveData.value = newProperties
        } else {
            _propertiesLiveData.value = _propertiesLiveData.value.orEmpty() + newProperties
        }
    }

    // Check if more properties can be loaded
    fun canLoadMore(): Boolean = canLoadMore
}