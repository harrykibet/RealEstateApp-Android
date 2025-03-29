package com.application.real_estate_app.core_network.interfaces

import com.application.real_estate_app.core_model.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsRemoteDataSource {
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