package com.estatia.realestate.apps.core.common.exceptions

sealed class CommentException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object UserNotAuthenticated :
        CommentException(
            "User must be authenticated to comment"
        )

    data object CommentNotFound :
        CommentException(
            "Comment not found"
        )

    data class UserLookupFailed(
        val exception: AppException
    ): CommentException(
        "Unable to load user profile",
        exception
    )

    data class InvalidComment(val reason: String) : CommentException(reason)


    data object PermissionDenied :
        CommentException(
            "Permission denied"
        )

    data class Unknown(
        val throwable:Throwable
    ): CommentException(
        "Unknown comment error",
        throwable
    )


    data object UserProfileMissing :
        CommentException(
            "User profile required"
        )


    data object EmptyComment :
        CommentException(
            "Comment cannot be empty"
        )


    data object CommentSubmissionFailed :
        CommentException(
            "Failed to submit comment"
        )
}
