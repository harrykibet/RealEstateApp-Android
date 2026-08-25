package com.estatia.realestate.apps.core.testing_network.scenarios

import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior

/**
 * Entry point for building reusable test scenarios that compose multiple fakes and chaos implementations.
 */
class EstatiaTestScenario private constructor() {
    
    val networkChaos = NetworkChaosController()
    var authBehavior: AuthBehavior = AuthBehavior.Authenticated
    var databaseBehavior: DatabaseBehavior = DatabaseBehavior.Success

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
         * Builds a scenario where the database is locked (e.g., during a backup or migration).
         */
        fun databaseLocked(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                databaseBehavior = DatabaseBehavior.Locked
            }
        }

        /**
         * Builds a scenario where the server succeeds but the client times out before receiving the ACK.
         * Useful for testing idempotency of side-effect operations like payments.
         */
        fun serverSuccessClientTimeout(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                networkChaos.script(NetworkBehavior.ServerSuccessClientTimeout)
            }
        }

        /**
         * Builds a scenario where the network fails with a 503 Service Unavailable error.
         */
        fun serverUnavailable(): EstatiaTestScenario {
            return EstatiaTestScenario().apply {
                networkChaos.script(NetworkBehavior.HttpError(503))
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
