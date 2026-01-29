package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.model.feature.Comment
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.common.errors.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    private val remoteDataSource: ICommentsRemoteDataSource
) : ICommentsRepository {

    override fun observeComments(
        propertyId: String
    ): Flow<List<Comment>> {
        return remoteDataSource.observeComments(propertyId)
    }

    override suspend fun submitComment(
        propertyId: String,
        comment: Comment
    ): Result<Unit> {
        return remoteDataSource.submitComment(propertyId, comment)
    }
}
