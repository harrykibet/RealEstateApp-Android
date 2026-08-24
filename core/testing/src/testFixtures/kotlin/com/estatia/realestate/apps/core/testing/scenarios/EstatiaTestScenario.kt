package com.estatia.realestate.apps.core.testing.scenarios

import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController

/**
 * Entry point for building reusable test scenarios that compose multiple fakes and chaos implementations.
 */
class EstatiaTestScenario private constructor() {
    
    val networkChaos = NetworkChaosController()

    companion object {
        /**
         * Builds a scenario where the network is completely offline.
         */
        fun networkOffline(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                networkChaos.script(NetworkBehavior.Offline)
            }
        }

        /**
         * Builds a scenario where the network times out once and then succeeds.
         */
        fun networkTimeoutRetry(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                networkChaos.script(NetworkBehavior.Timeout, NetworkBehavior.Success)
            }
        }
    }
}
