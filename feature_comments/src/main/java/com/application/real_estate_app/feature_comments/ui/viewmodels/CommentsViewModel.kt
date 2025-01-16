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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val api: ICommentsApi
) : ViewModel() {

    private val _comments = MutableLiveData<List<Comment?>>()
    val comments: LiveData<List<Comment?>> get() = _comments

    private val _commentSubmitStatus = MutableLiveData<Boolean>()
    val commentSubmitStatus: LiveData<Boolean> get() = _commentSubmitStatus

    private val _noCommentsPlaceholderVisibility = MutableLiveData<Int>()
    val noCommentsPlaceholderVisibility: LiveData<Int> get() = _noCommentsPlaceholderVisibility

    // Start listening for comments on a specific property
    fun startListeningForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            api.listenForComments(
                propertyId,
                onFailure = { exception ->
                    Log.e("CommentsViewModel", "Error listening for comments", exception)
                    onFailure(exception)
                }
            ).catch { exception ->
                Log.e("CommentsViewModel", "Error in flow listening for comments", exception)
                onFailure(exception as Exception)
            }.collect { commentsList ->
                _comments.postValue(commentsList)
                _noCommentsPlaceholderVisibility.value =
                    if (commentsList.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    // Submit a comment for a property
    fun submitComment(
        propertyId: String,
        comment: Comment,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = api.submitComment(
                    propertyId,
                    comment,
                    onFailure = { exception ->
                        Log.e("CommentsViewModel", "Error submitting comment", exception)
                        onFailure(exception)
                    }
                )
                _commentSubmitStatus.value = success ?: false
                if (success == true) {
                    Log.d("CommentsViewModel", "Comment submitted successfully")
                } else {
                    Log.e("CommentsViewModel", "Failed to submit comment")
                }
            } catch (e: Exception) {
                _commentSubmitStatus.value = false
                Log.e("CommentsViewModel", "Error submitting comment", e)
                onFailure(e)
            }
        }
    }
}
