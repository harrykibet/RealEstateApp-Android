package com.estatia.realestate.apps.core.model.system

import com.estatia.realestate.apps.core.architecture.annotations.Data

@Data.NetworkState
sealed class NetworkState {

    data object Connected : NetworkState()

    data object PoorConnection : NetworkState()

    data object NoInternet : NetworkState()
}
