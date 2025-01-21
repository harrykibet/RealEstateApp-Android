package com.application.real_estate_app.core.interfaces

interface INetworkHandler {
    suspend  fun <T> safeApiCallWithRetry(
        maxRetries: Int = 3,
        retryDelayMs: Long = 3000,
        apiCall: suspend () -> T,
        onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T?

    fun <T> safeApiCall(
        apiCall: () -> T,
        onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T?

    suspend fun <T> safeApiCallSuspend(
        apiCall: suspend () -> T,
        onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T?
}