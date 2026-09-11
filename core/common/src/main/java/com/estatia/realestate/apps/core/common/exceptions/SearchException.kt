package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
sealed class SearchException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data object EmptyQuery :
        SearchException(
            "Search query is empty"
        )


@DomainModel
    data object InvalidQuery :
        SearchException(
            "Invalid search query"
        )


@DomainModel
    data object SearchFailed :
        SearchException(
            "Search failed"
        )

@DomainModel
    data object QueryFailed:
        SearchException(
            "Search failed"
        )



@DomainModel
    data object NoResults:
        SearchException(
            "No search results"
        )


@DomainModel
    data class Unknown(
        val throwable:Throwable
    ):SearchException(
        "Unknown search error",
        throwable
    )
}
