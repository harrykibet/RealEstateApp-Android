package com.application.real_estate_app.core.common.errors

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: SecurityException) : Result<Nothing>()
}