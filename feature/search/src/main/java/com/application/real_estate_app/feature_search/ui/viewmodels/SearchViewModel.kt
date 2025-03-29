package com.application.real_estate_app.feature_search.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.core_data.interfaces.ISearchRepository
import com.application.real_estate_app.core_model.Property
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: ISearchRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Property>>()
    val searchResults: LiveData<List<Property>> = _searchResults

    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory

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
