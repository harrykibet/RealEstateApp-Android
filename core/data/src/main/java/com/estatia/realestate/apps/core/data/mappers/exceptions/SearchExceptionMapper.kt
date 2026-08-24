package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.SearchException
import javax.inject.Inject

/**
 * Specialized mapper for translating search-related infrastructure failures.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map platform errors to [SearchException] domain failures.
 * - Concurrency: Stateless and thread-safe.
 */
internal class SearchExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<SearchException>(
    notFound = { SearchException.NoResults },
    permissionDenied = { SearchException.QueryFailed }, // Search doesn't have a specific permission denied yet
    creationFailed = { SearchException.QueryFailed },
    unknown = { SearchException.Unknown(it) }
)
