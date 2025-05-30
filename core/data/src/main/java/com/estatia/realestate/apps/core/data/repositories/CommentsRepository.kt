package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.model.feature.Comment
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    private val remoteDataSource: ICommentsRemoteDataSource
) : ICommentsRepository {

    override fun listenForComments(
        propertyId: String,
        onFailure: (Exception) -> Unit
    ): Flow<List<Comment?>> {
        return remoteDataSource.listenForComments(propertyId, onFailure)
    }

    override suspend fun submitComment(
        propertyId: String,
        comment: Comment,
        onFailure: (Exception) -> Unit
    ): Boolean? {
        return remoteDataSource.submitComment(propertyId, comment, onFailure)
    }
}
