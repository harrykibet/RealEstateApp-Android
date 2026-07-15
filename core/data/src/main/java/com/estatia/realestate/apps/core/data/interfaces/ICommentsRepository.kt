package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import kotlinx.coroutines.flow.Flow

interface ICommentsRepository {
    fun observeComments(
        propertyId: String
    ): Flow<Result<List<CommentDomainModel>>>

    suspend fun submitComment(
        propertyId: String,
        message: String
    ): Result<Unit>
}