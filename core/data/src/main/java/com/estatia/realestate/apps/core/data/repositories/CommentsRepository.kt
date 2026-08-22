package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.domain.repository.ICommentsRepository
import com.estatia.realestate.apps.core.model.feature.CommentDomainModel
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.common.exceptions.getOrNull
import com.estatia.realestate.apps.core.data.mappers.firestore.FirestoreCommentMapper
import com.estatia.realestate.apps.core.data.mappers.room.RoomCommentMapper
import com.estatia.realestate.apps.core.database.interfaces.IPropertyLocalDataSource
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.common.exceptions.CommentException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.common.IContentSafetyService
import com.estatia.realestate.apps.core.model.engagement.SafetyResult
import com.estatia.realestate.apps.core.data.util.translateCommentFailures
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


internal class CommentsRepository @Inject constructor(
    private val remoteDataSource: ICommentsRemoteDataSource,
    private val localDataSource: IPropertyLocalDataSource,
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository,
    private val engagementRepository: IEngagementRepository,
    private val contentSafetyService: IContentSafetyService,
    private val exceptionTranslator: IExceptionTranslator
) : ICommentsRepository {


    override fun observeComments(
        propertyId: String
    ): Flow<AppResult<List<CommentDomainModel>>> {
        return remoteDataSource
            .observeComments(propertyId)
            .map { result ->
                result.map { comments ->
                    comments.map(FirestoreCommentMapper::toDomain)
                }.translateCommentFailures(exceptionTranslator)
            }
            .onStart {
                // 🏎️ Report engagement signal for personalization
                engagementRepository.reportInteraction(propertyId, EngagementAction.COMMENT_OPEN)

                val cached = localDataSource.getCachedComments(propertyId).getOrNull()
                if (!cached.isNullOrEmpty()) {
                    emit(AppResult.Success(cached.map(RoomCommentMapper::toDomain)))
                }
            }
            .onEach { result ->
                if (result is AppResult.Success) {
                    localDataSource.cacheComments(result.data.map(RoomCommentMapper::toEntity))
                }
            }
    }


    override suspend fun submitComment(
        propertyId: String,
        message: String
    ): AppResult<Unit> {

        // 🛡️ Proactive On-Device Moderation
        val safetyResult = contentSafetyService.validateText(message)
        if (safetyResult is SafetyResult.Flagged) {
            return AppResult.Error(CommentException.InvalidComment(safetyResult.reason))
        }

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
