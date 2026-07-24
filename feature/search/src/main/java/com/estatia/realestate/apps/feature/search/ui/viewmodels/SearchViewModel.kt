package com.estatia.realestate.apps.feature.search.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.data.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.feature.search.PlacesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: ISearchRepository,
    private val placesManager: PlacesManager
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<PropertyDomainModel>>()
    val searchResults: LiveData<List<PropertyDomainModel>> = _searchResults

    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    fun initializePlaces(context: Context) {
        placesManager.initialize(context)
    }

    fun searchProperties(query: String, limit: Int) {
        viewModelScope.launch {
            when (val result = searchRepository.searchProperties(query, limit)) {
                is AppResult.Success -> {
                    _searchResults.value = result.data
                    _error.value = null
                }
                is AppResult.Error -> {
                    _error.value = result.exception.message
                }
            }
        }
    }

    fun loadNearbyProperties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ) {
        viewModelScope.launch {
            when (val result = searchRepository.getNearbyProperties(latitude, longitude, radiusKm)) {
                is AppResult.Success -> {
                    _searchResults.value = result.data
                    _error.value = null
                }
                is AppResult.Error -> {
                    _error.value = result.exception.message
                }
            }
        }
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            when (val result = searchRepository.getSearchHistory()) {
                is AppResult.Success -> {
                    _searchHistory.value = result.data
                }
                is AppResult.Error -> {
                    // Handle error if needed
                }
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchRepository.clearSearchHistory()
            _searchHistory.value = emptyList()
        }
    }
}
