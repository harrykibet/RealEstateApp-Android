package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class SearchException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data object EmptyQuery :
        SearchException(
            "Search query is empty"
        )


@Helper
    data object InvalidQuery :
        SearchException(
            "Invalid search query"
        )


@Helper
    data object SearchFailed :
        SearchException(
            "Search failed"
        )

@Helper
    data object QueryFailed:
        SearchException(
            "Search failed"
        )



@Helper
    data object NoResults:
        SearchException(
            "No search results"
        )


@Helper
    data class Unknown(
        val throwable:Throwable
    ):SearchException(
        "Unknown search error",
        throwable
    )
}
