package com.estatia.realestate.apps.core.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.getOrThrow
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
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
    private val logger: ILogger,
) : ViewModel() {

    private  val currentUserId: String? = authApi.getCurrentUserId()

    private val _propertyLiveData = MutableLiveData<PropertyDomainModel?>()
    val propertyLiveData: LiveData<PropertyDomainModel?> get() = _propertyLiveData

    private val _likedStatus = MutableLiveData<LikeStatus>()
    val likedStatus: LiveData<LikeStatus> get() = _likedStatus

    private val _likedProperties = MutableLiveData<List<PropertyDomainModel>>()
    val likedProperties: LiveData<List<PropertyDomainModel>> get() = _likedProperties

    init {
        loadLikedProperties()
    }

    // Load liked properties for the current user
    fun loadLikedProperties() {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    val result = api.fetchLikedProperties(currentUserId)
                    _likedProperties.postValue(result.getOrThrow())
                } catch (e: Exception) {
                    log("Error loading liked properties: ${e.message}")
                }
            }
        }
    }

    // Toggle like/unlike status for a property
    fun toggleLikeProperty(propertyId: String) {
        val userId = currentUserId ?: return

        viewModelScope.launch {
            val isCurrentlyLiked =
                _likedProperties.value?.any { it.id.value == propertyId } == true

            try {
                val result = if (isCurrentlyLiked) {
                    api.unlikeProperty(userId, propertyId)
                } else {
                    api.likeProperty(userId, propertyId)
                }

                when (result) {
                    is AppResult.Success -> {
                        _likedStatus.value =
                            if (isCurrentlyLiked) LikeStatus.UNLIKE_SUCCESS
                            else LikeStatus.LIKE_SUCCESS

                        // Refresh source of truth
                        loadLikedProperties()
                    }
                    is AppResult.Error -> {
                        _likedStatus.value =
                            if (isCurrentlyLiked) LikeStatus.UNLIKE_ERROR
                            else LikeStatus.LIKE_ERROR
                        
                        log("Error updating like state: ${result.exception.message}")
                    }
                }

            } catch (e: Exception) {
                _likedStatus.value =
                    if (isCurrentlyLiked) LikeStatus.UNLIKE_ERROR
                    else LikeStatus.LIKE_ERROR

                log("Error updating like state: ${e.message}")
            }
        }
    }


    // Fetch a single property by its ID
    fun fetchPropertyById(propertyId: String) {
        viewModelScope.launch {
            try {
                when (val result = api.getPropertyById(propertyId)) {
                    is AppResult.Success -> {
                        _propertyLiveData.value = result.data
                    }
                    is AppResult.Error -> {
                        log("Failed to fetch property: ${result.exception.message}")
                    }
                }
            } catch (e: Exception) {
                log("Failed to fetch property: ${e.message}")
            }
        }
    }

    private fun log(message: String) {
        logger.e(tag = "PropertyViewModel", message = message)
    }
}
