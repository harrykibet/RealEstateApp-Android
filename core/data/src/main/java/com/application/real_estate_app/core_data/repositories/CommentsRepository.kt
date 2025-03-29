package com.application.real_estate_app.core_data.repositories

import com.application.real_estate_app.core_data.interfaces.ICommentsRepository
import com.application.real_estate_app.core_model.Comment
import com.application.real_estate_app.core_network.interfaces.ICommentsRemoteDataSource
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
