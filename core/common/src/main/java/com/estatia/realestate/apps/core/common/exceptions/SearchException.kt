package com.estatia.realestate.apps.core.common.exceptions

sealed class SearchException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object EmptyQuery :
        SearchException(
            "Search query is empty"
        )


    data object InvalidQuery :
        SearchException(
            "Invalid search query"
        )


    data object SearchFailed :
        SearchException(
            "Search failed"
        )
}