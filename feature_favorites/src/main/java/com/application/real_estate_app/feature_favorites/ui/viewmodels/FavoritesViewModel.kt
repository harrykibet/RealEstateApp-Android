package com.application.real_estate_app.feature_favorites.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.core.data_utils.models.Property
import com.application.real_estate_app.core.interfaces.IAuthApiCore
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFavoritesApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LikeStatus {
    LIKE_SUCCESS,    // Property liked successfully
    UNLIKE_SUCCESS,  // Property unliked successfully
    LIKE_ERROR,      // Error while liking the property
    UNLIKE_ERROR     // Error while unliking the property
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val api: IFavoritesApi,
    private val authApi: IAuthApiCore
) : ViewModel() {

    private  val currentUserId: String? = authApi.getCurrentUserId()

    private val _likedProperties = MutableLiveData<List<Property>>()
    val likedProperties: LiveData<List<Property>> get() = _likedProperties

    private val _likedStatus = MutableLiveData<LikeStatus>()
    val likedStatus: LiveData<LikeStatus> get() = _likedStatus

    private val _propertyLiveData = MutableLiveData<Property?>()
    val propertyLiveData: LiveData<Property?> get() = _propertyLiveData

    // Load liked properties for the current user
    fun loadLikedProperties() {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    val likedProperties = api.fetchLikedProperties(currentUserId)
                    _likedProperties.postValue(likedProperties)
                } catch (e: Exception) {
                    Log.e("PropertyViewModel", "Error loading liked properties", e)
                }
            }
        }
    }

    // Toggle like/unlike status for a property
    fun toggleLikeProperty(propertyId: String) {
        var isLiked = false  //Default value
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    isLiked = _likedProperties.value?.any { it.id == propertyId } ?: false
                    val success = api.toggleLikeProperty(currentUserId, propertyId)

                    if (success) {
                        _likedStatus.value = if (isLiked) LikeStatus.UNLIKE_SUCCESS else LikeStatus.LIKE_SUCCESS
                        loadLikedProperties() // Refresh the liked properties list
                    } else {
                        _likedStatus.value = if (isLiked) LikeStatus.UNLIKE_ERROR else LikeStatus.LIKE_ERROR
                    }
                } catch (e: Exception) {
                    _likedStatus.value = if (isLiked) LikeStatus.UNLIKE_ERROR else LikeStatus.LIKE_ERROR
                    Log.e("PropertyViewModel", "Error toggling like property", e)
                }
            }
        }
    }

    // Fetch a single property by its ID
    fun fetchPropertyById(propertyId: String) {
        viewModelScope.launch {
            try {
                val property = api.getPropertyById(propertyId)
                if (property != null) {
                    _propertyLiveData.value = property
                } else {
                    Log.e("PropertyViewModel", "Property not found")
                }
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Failed to fetch property", e)
            }
        }
    }
}