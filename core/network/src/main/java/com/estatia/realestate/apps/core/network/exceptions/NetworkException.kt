package com.estatia.realestate.apps.core.network.exceptions

sealed class NetworkException(
    message: String
) : Exception(message) {

    data object NoInternet :
        NetworkException("No internet connection")

    data object PoorConnection :
        NetworkException("Poor network connection")
}