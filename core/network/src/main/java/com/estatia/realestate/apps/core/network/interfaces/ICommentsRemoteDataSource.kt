package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.network.db_entities.CommentEntity
import kotlinx.coroutines.flow.Flow

interface ICommentsRemoteDataSource {
    fun observeComments(
        propertyId: String
    ): Flow<List<CommentEntity>>

    suspend fun submitComment(
        comment: CommentEntity
    ): Result<Unit>
}