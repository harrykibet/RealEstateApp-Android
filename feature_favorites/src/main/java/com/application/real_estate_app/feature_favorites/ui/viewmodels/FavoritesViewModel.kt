package com.application.real_estate_app.feature_favorites.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.core.domain.models.Property
import com.application.real_estate_app.core.domain.interfaces.AuthRepoInterface
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.feature_favorites.domain.interfaces.IFavoritesRepo
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
@Suppress("UNUSED")
class FavoritesViewModel @Inject constructor(
    private val api: IFavoritesRepo,
    authApi: AuthRepoInterface,
    private val logger: LoggerInterface
) : ViewModel() {

    private  val currentUserId: String? = authApi.getCurrentUserId()

    private val _likedProperties = MutableLiveData<List<Property>?>()
    val likedProperties: LiveData<List<Property>?> get() = _likedProperties

    private val _likedStatus = MutableLiveData<LikeStatus>()
    val likedStatus: LiveData<LikeStatus> get() = _likedStatus

    private val _propertyLiveData = MutableLiveData<Property?>()
    val propertyLiveData: LiveData<Property?> get() = _propertyLiveData

    // Load liked properties for the current user
    fun loadLikedProperties(onFailure: (Exception) -> Unit) {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    val likedProperties = api.fetchLikedProperties(currentUserId, onFailure)
                    _likedProperties.postValue(likedProperties)
                } catch (e: Exception) {
                    log("Error loading liked properties: ${e.message}")
                }
            }
        }
    }

    // Toggle like/unlike status for a property
    fun toggleLikeProperty(propertyId: String, onFailure: (Exception) -> Unit) {
        var isLiked = false  //Default value
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    isLiked = _likedProperties.value?.any { it.id == propertyId } ?: false
                    val success = api.toggleLikeProperty(currentUserId, propertyId, onFailure)

                    if (success) {
                        _likedStatus.value = if (isLiked) LikeStatus.UNLIKE_SUCCESS else LikeStatus.LIKE_SUCCESS
                        loadLikedProperties(onFailure) // Refresh the liked properties list
                    } else {
                        _likedStatus.value = if (isLiked) LikeStatus.UNLIKE_ERROR else LikeStatus.LIKE_ERROR
                    }
                } catch (e: Exception) {
                    _likedStatus.value = if (isLiked) LikeStatus.UNLIKE_ERROR else LikeStatus.LIKE_ERROR
                    log("Error toggling like property: ${e.message}")
                }
            }
        }
    }

    // Fetch a single property by its ID
    fun fetchPropertyById(propertyId: String, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val property = api.getPropertyById(propertyId, onFailure)
                if (property != null) {
                    _propertyLiveData.value = property
                } else {
                    log("Property not found")
                }
            } catch (e: Exception) {
                log("Failed to fetch property: ${e.message}")
            }
        }
    }

    private fun log(message: String?){
        logger.e("PropertyViewModel: $message")
    }
}