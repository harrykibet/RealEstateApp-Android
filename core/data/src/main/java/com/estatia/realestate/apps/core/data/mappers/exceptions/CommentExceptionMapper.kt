package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.CommentException
import javax.inject.Inject

/**
 * Specialized mapper for translating comment-related infrastructure failures.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map platform errors to [CommentException] domain failures.
 * - Concurrency: Stateless and thread-safe.
 */
internal class CommentExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<CommentException>(
    notFound = { CommentException.CommentNotFound },
    permissionDenied = { CommentException.PermissionDenied },
    creationFailed = { CommentException.CommentSubmissionFailed },
    unknown = { CommentException.Unknown(it) }
)
