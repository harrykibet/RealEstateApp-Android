package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.CommentException
import javax.inject.Inject

/**
 * Maps infrastructure exceptions to [CommentException].
 */
class CommentExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<CommentException>(
    notFound = { CommentException.CommentNotFound },
    permissionDenied = { CommentException.PermissionDenied },
    creationFailed = { CommentException.CommentSubmissionFailed },
    unknown = { CommentException.Unknown(it) }
)
