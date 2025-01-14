package com.application.real_estate_app.feature_comments.domain.interfaces

import com.application.real_estate_app.core.data_utils.data_models.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsApi {
    // Listen for Comments
    fun listenForComments(
        propertyId: String,
        onError: (Exception) -> Unit
    ): Flow<List<Comment?>>

    // Submit Comment
    suspend fun submitComment(
        propertyId: String,
        comment: Comment
    ): Boolean

}