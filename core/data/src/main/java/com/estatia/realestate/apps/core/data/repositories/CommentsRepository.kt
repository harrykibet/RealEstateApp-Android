package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.model.feature.Comment
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.data.mappers.RemoteCommentMapper
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    private val remoteDataSource: ICommentsRemoteDataSource,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ICommentsRepository {

    override fun observeComments(
        propertyId: String
    ): Flow<List<Comment>> {
        return remoteDataSource.observeComments(propertyId).map { comments ->
            comments.map { RemoteCommentMapper.toDomain(it) }
        }
    }


    override suspend fun submitComment(
        propertyId: String,
        message: String
    ): Result<Unit> {

        val userId = authRepository.getCurrentUserId()
            ?: return Result.Error(IllegalStateException("User not authenticated"))

        val user = userRepository.getUserById(userId)
            ?: return Result.Error(IllegalStateException("User not found"))


        val comment = CommentEntityModel(
            id = null,
            propertyId = propertyId,
            authorId = userId,
            authorName = user.name.orEmpty(),
            message = message,
            timeStamp = System.currentTimeMillis()
        )

        return remoteDataSource.submitComment(comment)
    }
}
