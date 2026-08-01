package com.estatia.realestate.apps.feature.comments.state

import com.estatia.realestate.apps.core.model.feature.CommentDomainModel

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<CommentDomainModel> = emptyList(),
    val input: String = "",
    val error: String? = null,
    val isSending: Boolean = false,
)
