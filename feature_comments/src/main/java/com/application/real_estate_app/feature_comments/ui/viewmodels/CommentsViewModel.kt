package com.application.real_estate_app.feature_comments.ui.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.real_estate_app.core.data_utils.data_models.Comment
import com.application.real_estate_app.feature_comments.domain.interfaces.ICommentsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: ICommentsApi
) : ViewModel() {

    private val _comments = MutableLiveData<List<Comment?>>()
    val comments: LiveData<List<Comment?>> get() = _comments

    private val _commentSubmitStatus = MutableLiveData<Boolean>()
    val commentSubmitStatus: LiveData<Boolean> get() = _commentSubmitStatus

    private val _noCommentsPlaceholderVisibility = MutableLiveData<Int>()
    val noCommentsPlaceholderVisibility: LiveData<Int> get() = _noCommentsPlaceholderVisibility

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