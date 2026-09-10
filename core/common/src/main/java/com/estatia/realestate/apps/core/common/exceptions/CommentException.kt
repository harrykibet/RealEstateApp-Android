package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class CommentException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data object UserNotAuthenticated :
        CommentException(
            "User must be authenticated to comment"
        )

@Helper
    data object CommentNotFound :
        CommentException(
            "Comment not found"
        )

@Helper
    data class UserLookupFailed(
        val exception: AppException
    ): CommentException(
        "Unable to load user profile",
        exception
    )

@Helper
    data class InvalidComment(val reason: String) : CommentException(reason)


    data object PermissionDenied :
        CommentException(
            "Permission denied"
        )

@Helper
    data class Unknown(
        val throwable:Throwable
    ): CommentException(
        "Unknown comment error",
        throwable
    )


@Helper
    data object UserProfileMissing :
        CommentException(
            "User profile required"
        )


@Helper
    data object EmptyComment :
        CommentException(
            "Comment cannot be empty"
        )


@Helper
    data object CommentSubmissionFailed :
        CommentException(
            "Failed to submit comment"
        )
}
