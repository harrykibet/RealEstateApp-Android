package com.application.real_estate_app.feature_comments.domain.interfaces

import com.application.real_estate_app.core.domain.models.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsApi {

    fun listenForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Flow<List<Comment?>>

    suspend fun submitComment(
        propertyId: String,
        comment: Comment,
        onFailure: (Exception) -> Unit
    ): Boolean?
}