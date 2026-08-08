package com.estatia.realestate.apps.core.network.sources.aws

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * AWS implementation of [ICommentsRemoteDataSource].
 */
internal class AwsCommentsRemoteDataSource @Inject constructor() : ICommentsRemoteDataSource {
    override suspend fun submitComment(comment: CommentEntityModel): AppResult<Unit> {
        // Future: Amplify.API.mutate(ModelMutation.create(comment))
        return AppResult.Success(Unit)
    }

    override fun observeComments(propertyId: String): Flow<AppResult<List<CommentEntityModel>>> {
        // Future: Amplify.API.subscribe(ModelSubscription.onCreate(Comment::class.java))
        return flowOf(AppResult.Success(emptyList()))
    }
}
