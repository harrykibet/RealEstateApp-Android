package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
sealed class CommentException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data object UserNotAuthenticated :
        CommentException(
            "User must be authenticated to comment"
        )

@DomainModel
    data object CommentNotFound :
        CommentException(
            "Comment not found"
        )

@DomainModel
    data class UserLookupFailed(
        val exception: AppException
    ): CommentException(
        "Unable to load user profile",
        exception
    )

@DomainModel
    data class InvalidComment(val reason: String) : CommentException(reason)


@DomainModel
    data object PermissionDenied :
        CommentException(
            "Permission denied"
        )

@DomainModel
    data class Unknown(
        val throwable:Throwable
    ): CommentException(
        "Unknown comment error",
        throwable
    )


@DomainModel
    data object UserProfileMissing :
        CommentException(
            "User profile required"
        )


@DomainModel
    data object EmptyComment :
        CommentException(
            "Comment cannot be empty"
        )


@DomainModel
    data object CommentSubmissionFailed :
        CommentException(
            "Failed to submit comment"
        )
}
