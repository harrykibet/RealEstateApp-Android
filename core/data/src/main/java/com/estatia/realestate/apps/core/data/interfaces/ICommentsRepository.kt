package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.model.feature.Comment
import kotlinx.coroutines.flow.Flow

interface ICommentsRepository {
    fun observeComments(
        propertyId: String
    ): Flow<List<Comment>>

    suspend fun submitComment(
        propertyId: String,
        comment: Comment
    ): Result<Unit>
}