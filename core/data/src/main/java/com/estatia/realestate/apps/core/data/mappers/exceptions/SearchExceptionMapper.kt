package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.SearchException
import javax.inject.Inject

/**
 * Maps infrastructure exceptions to [SearchException].
 */
class SearchExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<SearchException>(
    notFound = { SearchException.NoResults },
    permissionDenied = { SearchException.QueryFailed }, // Search doesn't have a specific permission denied yet
    creationFailed = { SearchException.QueryFailed },
    unknown = { SearchException.Unknown(it) }
)
