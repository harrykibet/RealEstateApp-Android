package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import kotlinx.coroutines.flow.Flow

interface ICommentsRepository {
    fun observeComments(
        propertyId: String
    ): Flow<AppResult<List<CommentDomainModel>>>

    suspend fun submitComment(
        propertyId: String,
        message: String
    ): AppResult<Unit>
}