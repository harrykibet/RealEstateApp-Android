package com.estatia.realestate.apps.feature.comments.state

import com.estatia.realestate.apps.core.model.feature.Comment

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val input: String = "",
    val error: String? = null
)