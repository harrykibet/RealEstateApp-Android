package com.estatia.realestate.apps.core.testing.scenarios

import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController

/**
 * Entry point for building reusable test scenarios that compose multiple fakes and chaos implementations.
 */
class EstatiaTestScenario private constructor() {
    
    val networkChaos = NetworkChaosController()
    var authBehavior: AuthBehavior = AuthBehavior.Authenticated

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

        /**
         * Builds a scenario where the authentication token has expired.
         */
        fun authExpired(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                authBehavior = AuthBehavior.TokenExpired
            }
        }

        /**
         * Builds a scenario for testing concurrent token refresh.
         */
        fun concurrentTokenRefresh(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                authBehavior = AuthBehavior.TokenExpired
                // Additional setup for concurrency testing would go here
            }
        }
    }
}
