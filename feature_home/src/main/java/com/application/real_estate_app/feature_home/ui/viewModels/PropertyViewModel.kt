package com.application.real_estate_app.feature_home.ui.viewModels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.domain.interfaces.AuthRepository
import com.application.real_estate_app.domain.models.Comment
import com.application.real_estate_app.domain.models.Property
import com.application.real_estate_app.domain.interfaces.IPropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

enum class LikeStatus {
    LIKE_SUCCESS,    // Property liked successfully
    UNLIKE_SUCCESS,  // Property unliked successfully
    LIKE_ERROR,      // Error while liking the property
    UNLIKE_ERROR     // Error while unliking the property
}

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val repository: IPropertyRepository,
    authChecker: AuthRepository
) : ViewModel() {

    private  val currentUserId: String? = authChecker.getCurrentUserId()

    private val _propertyLiveData = MutableLiveData<Property?>()
    val propertyLiveData: LiveData<Property?> get() = _propertyLiveData

    private val _comments = MutableLiveData<List<Comment?>>()
    val comments: LiveData<List<Comment?>> get() = _comments

    private val _commentSubmitStatus = MutableLiveData<Boolean>()
    val commentSubmitStatus: LiveData<Boolean> get() = _commentSubmitStatus

    private val _likedStatus = MutableLiveData<LikeStatus>()
    val likedStatus: LiveData<LikeStatus> get() = _likedStatus

    private val _likedProperties = MutableLiveData<List<Property>>()
    val likedProperties: LiveData<List<Property>> get() = _likedProperties

    private val _noCommentsPlaceholderVisibility = MutableLiveData<Int>()
    val noCommentsPlaceholderVisibility: LiveData<Int> get() = _noCommentsPlaceholderVisibility

    init {
        loadLikedProperties()
    }

    // Load liked properties for the current user
    fun loadLikedProperties() {
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    val likedProperties = repository.fetchLikedProperties(currentUserId)
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
                    val success = repository.toggleLikeProperty(currentUserId, propertyId)

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
                val property = repository.getPropertyById(propertyId)
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

    // Start listening for comments on a specific property
    fun startListeningForComments(propertyId: String) {
        viewModelScope.launch {
            try {
                repository.listenForComments(propertyId, onError = { exception ->
                    Log.e("PropertyViewModel", "Error listening for comments", exception)
                }).catch { exception ->
                    Log.e("PropertyViewModel", "Error listening for comments", exception)
                }.collect { commentsList ->
                    _comments.postValue(commentsList)
                    _noCommentsPlaceholderVisibility.value = if (commentsList.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Error listening for comments", e)
            }
        }
    }

    // Submit a comment for a property
    fun submitComment(propertyId: String, comment: Comment) {
        viewModelScope.launch {
            try {
                val success = repository.submitComment(propertyId, comment)
                _commentSubmitStatus.value = success
                if (success) {
                    Log.d("PropertyViewModel", "Comment submitted successfully")
                } else {
                    Log.e("PropertyViewModel", "Failed to submit comment")
                }
            } catch (e: Exception) {
                _commentSubmitStatus.value = false
                Log.e("PropertyViewModel", "Error submitting comment", e)
            }
        }
    }
}