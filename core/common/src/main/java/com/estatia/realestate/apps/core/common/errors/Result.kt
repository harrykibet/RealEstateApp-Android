package com.estatia.realestate.apps.core.common.errors


sealed class Result<out T> {

    data class Success<out T>(
        val data: T
    ) : Result<T>()


    data class Failure(
        val exception: Exception
    ) : Result<Nothing>()
}



inline fun <T,R> Result<T>.map(
    transform: (T) -> R
): Result<R> {

    return when(this) {

        is Result.Success ->
            Result.Success(
                transform(data)
            )

        is Result.Failure ->
            this
    }
}



inline fun <T,R> Result<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (Exception) -> R
): R {

    return when(this) {

        is Result.Success ->
            onSuccess(data)

        is Result.Failure ->
            onFailure(exception)
    }
}



inline fun <T> Result<T>.mapFailure(
    transform: (Exception) -> Exception
): Result<T> {

    return when(this) {

        is Result.Success ->
            this

        is Result.Failure ->
            Result.Failure(
                transform(exception)
            )
    }
}