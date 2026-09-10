package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.CommentException
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import javax.inject.Inject

/**
 * Specialized mapper for translating comment-related infrastructure failures.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map platform errors to [CommentException] domain failures.
 * - Concurrency: Stateless and thread-safe.
 */
@Helper
internal class CommentExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<CommentException>(
    notFound = { CommentException.CommentNotFound },
    permissionDenied = { CommentException.PermissionDenied },
    creationFailed = { CommentException.CommentSubmissionFailed },
    unknown = { CommentException.Unknown(it) }
)
