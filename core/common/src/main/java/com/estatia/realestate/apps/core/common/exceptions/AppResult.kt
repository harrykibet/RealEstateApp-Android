package com.estatia.realestate.apps.core.common.exceptions


sealed class AppResult<out T> {


    data class Success<out T>(
        val data:T
    ):AppResult<T>()


    data class Error(
        val exception: AppException
    ):AppResult<Nothing>()
}


inline fun <T,R> AppResult<T>.map(
    transform: (T) -> R
): AppResult<R> {

    return when(this) {

        is AppResult.Success ->
            AppResult.Success(
                transform(data)
            )

        is AppResult.Error ->
            this
    }
}



inline fun <T,R> AppResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (AppException) -> R
): R {

    return when(this) {

        is AppResult.Success ->
            onSuccess(data)

        is AppResult.Error ->
            onError(exception)
    }
}



inline fun <T> AppResult<T>.mapError(
    transform: (AppException) -> AppException
): AppResult<T> {

    return when(this) {

        is AppResult.Success ->
            this

        is AppResult.Error ->
            AppResult.Error(
                transform(exception)
            )
    }
}


fun <T> AppResult<T>.getOrThrow(): T {
    return when(this) {
        is AppResult.Success -> data
        is AppResult.Error -> throw exception
    }
}
