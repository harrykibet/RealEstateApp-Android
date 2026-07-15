package com.estatia.realestate.apps.core.common.exceptions

sealed class CommentException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object UserNotAuthenticated :
        CommentException(
            "User must be authenticated to comment"
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