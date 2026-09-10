package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

@EntityModel
sealed class NetworkState {

    data object Connected : NetworkState()

    data object PoorConnection : NetworkState()

    data object NoInternet : NetworkState()
}
