package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.model.feature.Comment
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