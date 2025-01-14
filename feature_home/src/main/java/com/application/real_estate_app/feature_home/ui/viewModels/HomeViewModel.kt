package com.application.real_estate_app.feature_home.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.feature_home.domain.interfaces.IHomeApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: IHomeApi
) : ViewModel() {

    private val _propertiesLiveData = MutableLiveData<List<Property>>()
    val propertiesLiveData: LiveData<List<Property>> get() = _propertiesLiveData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val isLoadingMutable = MutableLiveData<Boolean>()
    //val isLoading: LiveData<Boolean> get() = isLoadingMutable

    private var lastVisibleDocument: String? = null
    private var canLoadMore = true

    // Fetch properties with api in a coroutine
    fun fetchProperties(isFirstLoad: Boolean, pageSize: Int) {
        if (!canLoadMore || isLoadingMutable.value == true) return // Prevent redundant fetches

        isLoadingMutable.value = true

        viewModelScope.launch {
            try {
                val result = api.fetchPropertiesPaginated(lastVisibleDocument, pageSize)
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
    private fun handleFetchedProperties(newProperties: List<Property>, isFirstLoad: Boolean) {
        if (isFirstLoad) {
            _propertiesLiveData.value = newProperties
        } else {
            _propertiesLiveData.value = _propertiesLiveData.value.orEmpty() + newProperties
        }
    }

    // Check if more properties can be loaded
    fun canLoadMore(): Boolean = canLoadMore
}