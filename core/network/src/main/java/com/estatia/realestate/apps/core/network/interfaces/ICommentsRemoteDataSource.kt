package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.feature.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsRemoteDataSource {
    fun observeComments(
        propertyId: String
    ): Flow<List<Comment>>

    suspend fun submitComment(
        comment: Comment
    ): Result<Unit>
}