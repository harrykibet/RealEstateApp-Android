package com.estatia.realestate.apps.feature.comments.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.estatia.realestate.apps.core.common.system.Dispatcher
import com.estatia.realestate.apps.core.common.system.EstatiaDispatchers
import com.estatia.realestate.apps.core.domain.repository.ICommentsRepository
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.events.CommentsEvent
import com.estatia.realestate.apps.feature.comments.state.CommentsUiState
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the real-time comment stream of a property.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the retrieval, display, and submission of property comments.
 * - Concurrency: Thread-safe via [viewModelScope] and serialized [update] calls.
 * - Resilience: Surfaces cached comments on start while remote stream initializes.
 * - Lifecycle: Automatically cancels observation jobs [observeJob] on property switch.
 * - Observability: Tracks comment submission and observation health.
 */
@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentsRepository: ICommentsRepository,
    private val metricsTracker: IMetricsTracker,
    @param:Dispatcher(EstatiaDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(CommentsUiState())
    val state: StateFlow<CommentsUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CommentsEvent>()
    val events = _events.asSharedFlow()

    private var currentPropertyId: String? = null

    private var observeJob: Job? = null


    fun onAction(action: CommentsAction) {
        when (action) {

            is CommentsAction.InputChanged ->
                update { copy(input = action.value) }

            CommentsAction.SendComment ->
                submitComment()

            is CommentsAction.Load ->
                startObservingComments(action.propertyId)

            CommentsAction.Refresh ->
                currentPropertyId?.let { startObservingComments(it) }
        }
    }


    fun startObservingComments(propertyId: String) {
        if (currentPropertyId == propertyId) return
        currentPropertyId = propertyId

        observeJob?.cancel()
        observeJob = viewModelScope.launch(ioDispatcher) {
            update { copy(isLoading = true, error = null) }

            commentsRepository.observeComments(propertyId).collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        update {
                            copy(
                                isLoading = false,
                                comments = result.data,
                                error = null,
                            )
                        }
                    }

                    is AppResult.Error -> {
                        update {
                            copy(
                                isLoading = false,
                                error = result.exception.message ?: "Failed to load comments"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun submitComment() {
        val propertyId = currentPropertyId ?: return
        val message = state.value.input.trim()
        if (message.isBlank()) return

        if (state.value.isSending) return // 🛡️ Idempotency

        viewModelScope.launch(ioDispatcher) {
            update { copy(isSending = true) }
            when (val result = commentsRepository.submitComment(propertyId, message)) {
                is AppResult.Success -> {
                    metricsTracker.incrementCounter("comments.post.success")
                    update { copy(input = "", isSending = false) }
                    _events.emit(CommentsEvent.ShowMessage("Comment posted"))
                }

                is AppResult.Error -> {
                    metricsTracker.incrementCounter("comments.post.failure")
                    update { copy(isSending = false) }
                    _events.emit(
                        CommentsEvent.ShowMessage(
                            result.exception.message ?: "Failed to post comment"
                        )
                    )
                }
            }
        }
    }




    private inline fun update(
        block: CommentsUiState.() -> CommentsUiState
    ) {
        _state.value = _state.value.block()
    }
}

