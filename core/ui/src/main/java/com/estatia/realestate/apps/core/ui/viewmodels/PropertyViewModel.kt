package com.estatia.realestate.apps.core.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.model.property.Property
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
class PropertyViewModel @Inject constructor(
    private val api: IPropertyRepository,
    authApi: IAuthRepository,
    private val logger: LoggerInterface
) : ViewModel() {

    private  val currentUserId: String? = authApi.getCurrentUserId()

    private val _propertyLiveData = MutableLiveData<Property?>()
    val propertyLiveData: LiveData<Property?> get() = _propertyLiveData

    private val _likedStatus = MutableLiveData<LikeStatus>()
    val likedStatus: LiveData<LikeStatus> get() = _likedStatus

    private val _likedProperties = MutableLiveData<List<Property>>()
    val likedProperties: LiveData<List<Property>> get() = _likedProperties

    init {
        loadLikedProperties{ exception ->
            log("Error initializing loading of liked properties: ${exception.message}")
        }
    }

    // Load liked properties for the current user
    fun loadLikedProperties(onFailure: (Exception) -> Unit) {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    val likedProperties = api.fetchLikedProperties(currentUserId, onFailure)
                    _likedProperties.postValue(likedProperties!!)
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

    private fun log(message: String) {
        logger.e("PropertyViewModel: $message")
    }
}