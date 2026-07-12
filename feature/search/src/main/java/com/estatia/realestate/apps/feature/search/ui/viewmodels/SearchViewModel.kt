package com.estatia.realestate.apps.feature.search.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.data.interfaces.ISearchRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.feature.search.PlacesManager
import com.google.android.gms.maps.GoogleMap
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


    fun initializePlaces(context: Context) {
        placesManager.initialize(context)
    }

    fun searchProperties(query: String, limit: Int) {
        viewModelScope.launch {
            try {
                val results = searchRepository.searchProperties(query, limit) { exception ->
                    // Handle failure
                }
                _searchResults.postValue(results)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun loadNearbyProperties(map: GoogleMap, userLat: Double, userLng: Double): Boolean {
        var nearbyPropertiesFound = false
        viewModelScope.launch {
            nearbyPropertiesFound = searchRepository.loadNearbyProperties(map, userLat, userLng)
        }
        return nearbyPropertiesFound
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            _searchHistory.value = searchRepository.getSearchHistory()
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchRepository.clearSearchHistory()
        }
    }
}
