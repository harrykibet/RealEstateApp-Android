package com.estatia.realestate.apps.core.common.errors

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(this.data))
        is Result.Error -> this // Keep the error unchanged
    }
}
