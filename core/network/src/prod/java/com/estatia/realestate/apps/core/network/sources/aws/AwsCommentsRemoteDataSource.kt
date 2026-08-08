package com.estatia.realestate.apps.core.network.sources.aws

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * AWS implementation of [ICommentsRemoteDataSource] (Skeleton).
 */
internal class AwsCommentsRemoteDataSource @Inject constructor() : ICommentsRemoteDataSource {
    override suspend fun submitComment(comment: CommentEntityModel): AppResult<Unit> = AppResult.Success(Unit)
    override fun observeComments(propertyId: String): Flow<AppResult<List<CommentEntityModel>>> = flowOf(AppResult.Success(emptyList()))
}
