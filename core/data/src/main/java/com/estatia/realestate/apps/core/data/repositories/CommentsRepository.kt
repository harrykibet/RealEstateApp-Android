package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.data.interfaces.ICommentsRepository
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestoreCommentMapper
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.common.exceptions.CommentException
import com.estatia.realestate.apps.core.data.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.data.interfaces.IExceptionTranslator
import com.estatia.realestate.apps.core.data.interfaces.IUserRepository
import com.estatia.realestate.apps.core.data.util.translateCommentFailures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CommentsRepository @Inject constructor(
    private val remoteDataSource: ICommentsRemoteDataSource,
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository,
    private val exceptionTranslator: IExceptionTranslator
) : ICommentsRepository {


    override fun observeComments(
        propertyId: String
    ): Flow<AppResult<List<CommentDomainModel>>> {


        return remoteDataSource
            .observeComments(propertyId)

            .map { result ->

                result

                    .map { comments ->

                        comments.map(
                            FirestoreCommentMapper::toDomain
                        )
                    }

                    .translateCommentFailures(
                        exceptionTranslator
                    )
            }
    }


    override suspend fun submitComment(
        propertyId: String,
        message: String
    ): AppResult<Unit> {


        val userId =
            authRepository.getCurrentUserId()
                ?: return AppResult.Error(
                    CommentException.UserNotAuthenticated
                )


        return when(
            val userResult =
                userRepository.getUserById(userId)
        ){

            is AppResult.Error ->
                AppResult.Error(
                    exception = userResult.exception
                )


            is AppResult.Success -> {

                val user =
                    userResult.data


                val comment =
                    CommentEntityModel(
                        id = null,
                        propertyId = propertyId,
                        authorId = userId,
                        authorName = user.name.orEmpty(),
                        message = message,
                        timeStamp = System.currentTimeMillis()
                    )


                remoteDataSource
                    .submitComment(comment)
                    .translateCommentFailures(
                        exceptionTranslator
                    )
            }
        }
    }
}
