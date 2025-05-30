package com.estatia.realestate.apps.core.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.common.system.NetworkUtils
import com.estatia.realestate.apps.core.network.interfaces.INetworkHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkHandler @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val networkUtils: NetworkUtils,
    private val logger: LoggerInterface
): INetworkHandler {

    /**
     * Safely executes a network call with retries by checking for network connectivity,
     * handling exceptions, and invoking a failure callback on error.
     *
     * @param maxRetries The maximum number of retry attempts.
     * @param retryDelayMs Delay between retries in milliseconds.
     * @param apiCall The suspending network call to execute.
     * @param onFailure Callback function to handle failure (exception).
     * @return The result of the network call or null if an error occurs or retries exhausted.
     */
    override suspend  fun <T> safeApiCallWithRetry(
        maxRetries: Int,
        retryDelayMs: Long,
        apiCall: suspend () -> T,
        onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
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
                    logger.d("NetworkHandler: Network available, checking internet connection...")

                    // Check if the available network has internet access
                    if (networkUtils.hasInternetAccess()) {
                        logger.d("NetworkHandler: Internet is available, proceeding with retries...")

                        retryJob?.cancel() // Cancel any ongoing retry job

                        // Retry logic in a new coroutine scope
                        retryJob = CoroutineScope(Dispatchers.IO).launch {
                            while (attempt < maxRetries) {
                                attempt++
                                try {
                                    result = apiCall() // Execute the network apiCall
                                    break // Success, exit the loop
                                } catch (e: Exception) {
                                    logger.e("NetworkHandler: Retry $attempt failed: ${e.message}")
                                    if (attempt >= maxRetries) {
                                        logger.e("NetworkHandler: Max retries reached, giving up.")
                                        onFailure(e) // Call onFailure callback on max retries
                                    } else {
                                        delay(retryDelayMs) // Delay before retrying
                                    }
                                }
                            }
                        }
                    } else {
                        logger.w("NetworkHandler: Network is available but no internet access.")
                        onFailure(Exception(Errors.NO_INTERNET_CONNECTION))
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    logger.w("NetworkHandler: Network lost, retrying not possible.")
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
     * @param apiCall The network call to execute.
     * @param onFailure Callback function to handle failure (exception).
     * @return The result of the network call or null if an error occurs.
     */
    override fun <T> safeApiCall(
        apiCall: () -> T,
        onFailure: (Exception) -> Unit
    ): T? {
        return try {
            when (val status = networkUtils.getNetworkStatus()) {
                is NetworkUtils.NetworkStatusResult.Connected -> apiCall() // Proceed if connected
                is NetworkUtils.NetworkStatusResult.PoorConnection -> {
                    logger.w("NetworkHandler: ${Errors.POOR_CONNECTION} (Status: $status)")
                    onFailure(Exception(Errors.POOR_CONNECTION))
                    null
                }
                is NetworkUtils.NetworkStatusResult.NoInternet -> {
                    logger.w("NetworkHandler: ${Errors.NO_INTERNET_CONNECTION} (Status: $status)")
                    onFailure(Exception(Errors.NO_INTERNET_CONNECTION))
                    null
                }
            }
        } catch (e: Exception) {
            logger.e("NetworkHandler: API call failed", e)
            onFailure(e)
            null
        }
    }


    /**
     * A suspend version of safeApiCall for use with coroutines, with failure callback.
     *
     * @param apiCall The suspend network call to execute.
     * @param onFailure Callback function to handle failure (exception).
     * @return The result of the network call or null if an error occurs.
     */
    override suspend fun <T> safeApiCallSuspend(
        apiCall: suspend () -> T,
        onFailure: (Exception) -> Unit // Add onFailure callback to handle errors
    ): T? {
        return withContext(Dispatchers.IO) {
            try {
                when (networkUtils.getNetworkStatus()) {
                    is NetworkUtils.NetworkStatusResult.Connected -> apiCall() // Proceed if connected
                    is NetworkUtils.NetworkStatusResult.PoorConnection -> {
                        logger.w("NetworkHandler: ${Errors.POOR_CONNECTION}")
                        onFailure(Exception(Errors.POOR_CONNECTION))
                        null // Notify poor connection
                    }

                    is NetworkUtils.NetworkStatusResult.NoInternet -> {
                        logger.w("NetworkHandler: ${Errors.NO_INTERNET_CONNECTION}")
                        onFailure(Exception(Errors.NO_INTERNET_CONNECTION))
                        null // Notify no internet
                    }
                }
            } catch (e: Exception) {
                logger.e("NetworkHandler: API call failed: ${e.message}")
                onFailure(e) // Call onFailure callback on failure
                null
            }
        }
    }
}