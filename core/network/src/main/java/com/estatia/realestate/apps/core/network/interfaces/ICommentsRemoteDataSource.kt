package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import kotlinx.coroutines.flow.Flow

interface ICommentsRemoteDataSource {
    fun observeComments(
        propertyId: String
    ): Flow<Result<List<CommentEntityModel>>>

    suspend fun submitComment(
        comment: CommentEntityModel
    ): Result<Unit>
}