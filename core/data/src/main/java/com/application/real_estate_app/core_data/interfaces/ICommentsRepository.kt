package com.application.real_estate_app.core_data.interfaces

import com.application.real_estate_app.core_model.feature.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsRepository {
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