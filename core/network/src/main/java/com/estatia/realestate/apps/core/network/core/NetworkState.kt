package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.architecture.annotations.EntityModel

@EntityModel
sealed class NetworkState {

@EntityModel
    data object Connected : NetworkState()

    data object PoorConnection : NetworkState()

@EntityModel
    data object NoInternet : NetworkState()
}
