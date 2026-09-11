package com.estatia.realestate.apps.core.model.system

@com.estatia.realestate.apps.core.architecture.annotations.NetworkState
sealed class NetworkState {

    data object Connected : NetworkState()

    data object PoorConnection : NetworkState()

    data object NoInternet : NetworkState()
}
