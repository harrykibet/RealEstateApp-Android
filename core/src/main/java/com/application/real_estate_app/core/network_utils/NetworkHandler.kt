package com.application.real_estate_app.core.network_utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.application.real_estate_app.core.errors.ErrorMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("UNUSED")
object NetworkHandler {

    /**
     * Safely executes a network call with retries by checking for network connectivity,
     * handling exceptions, and invoking a failure callback on error.
     *
     * @param connectivityManager The ConnectivityManager instance used to check network status and register callbacks.
     * @param maxRetries The maximum number of retry attempts.
     * @param retryDelayMs Delay between retries in milliseconds.
     * @param apiCall The suspending network call to execute.
     * @param onFailure Callback function to handle failure (exception).
     * @return The result of the network call or null if an error occurs or retries exhausted.
     */
    suspend inline fun <T> safeApiCallWithRetry(
        connectivityManager: ConnectivityManager,
        maxRetries: Int = 3,
        retryDelayMs: Long = 3000,
        crossinline apiCall: suspend () -> T,
        crossinline onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T? {
        return withContext(Dispatchers.IO) {
            var result: T? = null
            var attempt = 0

            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            var retryJob: Job? = null // Coroutine job to handle retry logic

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d("NetworkHandler", "Network available, checking internet connection...")

                    // Check if the available network has internet access
                    if (NetworkStatus.hasInternetAccess()) {
                        Log.d("NetworkHandler", "Internet is available, proceeding with retries...")

                        retryJob?.cancel() // Cancel any ongoing retry job

                        // Retry logic in a new coroutine scope
                        retryJob = CoroutineScope(Dispatchers.IO).launch {
                            while (attempt < maxRetries) {
                                attempt++
                                try {
                                    result = apiCall() // Execute the network apiCall
                                    break // Success, exit the loop
                                } catch (e: Exception) {
                                    Log.e("NetworkHandler", "Retry $attempt failed: ${e.message}")
                                    if (attempt >= maxRetries) {
                                        Log.e("NetworkHandler", "Max retries reached, giving up.")
                                        onFailure(e) // Call onFailure callback on max retries
                                    } else {
                                        delay(retryDelayMs) // Delay before retrying
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w("NetworkHandler", "Network is available but no internet access.")
                        onFailure(Exception(ErrorMessages.NO_INTERNET_CONNECTION))
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.w("NetworkHandler", "Network lost, retrying not possible.")
                    retryJob?.cancel() // Cancel retry job if network is lost
                    onFailure(Exception("Network lost"))
                }
            }

            // Register the network callback
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

            retryJob?.join() // Wait for the retry job to complete

            // Unregister the network callback after completion to avoid memory leaks
            connectivityManager.unregisterNetworkCallback(networkCallback)

            result // Return the result of the API call after retries
        }
    }

    /**
     * Safely executes a network call by checking for network connectivity,
     * handling exceptions, and invoking a failure callback on error.
     *
     * @param connectivityManager The connectivity manager used to check network status.
     * @param apiCall The network call to execute.
     * @param onFailure Callback function to handle failure (exception).
     * @return The result of the network call or null if an error occurs.
     */
    inline fun <T> safeApiCall(
        connectivityManager: ConnectivityManager,
        apiCall: () -> T,
        onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T? {
        return try {
            when (NetworkStatus.getNetworkStatus(connectivityManager)) {
                is NetworkStatus.NetworkStatusResult.Connected -> apiCall() // Proceed if connected
                is NetworkStatus.NetworkStatusResult.PoorConnection -> {
                    Log.w("NetworkHandler", ErrorMessages.POOR_CONNECTION)
                    onFailure(Exception(ErrorMessages.POOR_CONNECTION))
                    null // Notify poor connection
                }
                is NetworkStatus.NetworkStatusResult.NoInternet -> {
                    Log.w("NetworkHandler", ErrorMessages.NO_INTERNET_CONNECTION)
                    onFailure(Exception(ErrorMessages.NO_INTERNET_CONNECTION))
                    null // Notify no internet
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkHandler", "API call failed: ${e.message}")
            onFailure(e) // Call onFailure callback on failure
            null
        }
    }

    /**
     * A suspend version of safeApiCall for use with coroutines, with failure callback.
     *
     * @param connectivityManager The connectivity manager used to check network status.
     * @param apiCall The suspend network call to execute.
     * @param onFailure Callback function to handle failure (exception).
     * @return The result of the network call or null if an error occurs.
     */
    suspend inline fun <T> safeApiCallSuspend(
        connectivityManager: ConnectivityManager,
        crossinline apiCall: suspend () -> T,
        crossinline onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T? {
        return withContext(Dispatchers.IO) {
            try {
                when (NetworkStatus.getNetworkStatus(connectivityManager)) {
                    is NetworkStatus.NetworkStatusResult.Connected -> apiCall() // Proceed if connected
                    is NetworkStatus.NetworkStatusResult.PoorConnection -> {
                        Log.w("NetworkHandler", ErrorMessages.POOR_CONNECTION)
                        onFailure(Exception(ErrorMessages.POOR_CONNECTION))
                        null // Notify poor connection
                    }
                    is NetworkStatus.NetworkStatusResult.NoInternet -> {
                        Log.w("NetworkHandler", ErrorMessages.NO_INTERNET_CONNECTION)
                        onFailure(Exception(ErrorMessages.NO_INTERNET_CONNECTION))
                        null // Notify no internet
                    }
                }
            } catch (e: Exception) {
                Log.e("NetworkHandler", "API call failed: ${e.message}")
                onFailure(e) // Call onFailure callback on failure
                null
            }
        }
    }
}
