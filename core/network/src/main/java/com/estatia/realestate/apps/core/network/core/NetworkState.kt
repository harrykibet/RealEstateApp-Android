package com.estatia.realestate.apps.core.network.core

sealed class NetworkState {

    data object Connected : NetworkState()

    data object PoorConnection : NetworkState()

    data object NoInternet : NetworkState()
}
