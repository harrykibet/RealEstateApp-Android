package com.estatia.realestate.apps.feature.comments.ui.viewmodels


import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.data.interfaces.IUserRepository
import com.estatia.realestate.apps.core.model.feature.Comment
import com.estatia.realestate.apps.core.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val api: ICommentsRepository,
    private val userRepo: IUserRepository,
    private val logger: LoggerInterface
) : ViewModel() {

    private val _comments = MutableLiveData<List<Comment?>>()
    val comments: LiveData<List<Comment?>> get() = _comments

    private val _commentSubmitStatus = MutableLiveData<Boolean>()
    val commentSubmitStatus: LiveData<Boolean> get() = _commentSubmitStatus

    private val _noCommentsPlaceholderVisibility = MutableLiveData<Int>()
    val noCommentsPlaceholderVisibility: LiveData<Int> get() = _noCommentsPlaceholderVisibility

    private val userCache = mutableMapOf<String, User?>()

    // Start listening for comments on a specific property
    fun startListeningForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            api.listenForComments(
                propertyId,
                onFailure = { exception ->
                    log("Error listening for comments: ${exception.message}")
                    onFailure(exception)
                }
            ).catch { exception ->
                log("Error in flow listening for comments: ${exception.message}")
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
                        log("Error submitting comment: ${exception.message}")
                        onFailure(exception)
                    }
                )
                _commentSubmitStatus.value = success ?: false
                if (success == true) {
                    logger.d("CommentsViewModel: Comment submitted successfully")
                } else {
                    log("Failed to submit comment")
                }
            } catch (e: Exception) {
                _commentSubmitStatus.value = false
                log("Error submitting comment: ${e.message}")
                onFailure(e)
            }
        }
    }

    fun getUser(userId: String, onResult: (User?) -> Unit) {
        if (userCache.containsKey(userId)) {
            onResult(userCache[userId])
        } else {
            viewModelScope.launch {
                val user = userRepo.getUserById(userId)
                userCache[userId] = user
                onResult(user)
            }
        }
    }

    private fun log(message: String?) {
        logger.e("CommentsViewModel: $message")
    }
}
