package com.estatia.realestate.apps.core.testing_network.chaos

import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.chaos.database.DatabaseBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourceController
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.fake.source.FakePropertyRemoteDataSource
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeSearchRemoteDataSource
import javax.inject.Inject

/**
 * DSL for composing complex adversarial scenarios across multiple infrastructure controllers.
 * Standardizes high-level failure modes to keep feature tests readable.
 */
class EstatiaTestScenario @Inject constructor(
    private val networkChaos: NetworkChaosController,
    private val networkClient: INetworkClient,
    private val resourceController: ChaosResourceController,
    private val authFake: FakeAuthRemoteDataSource,
    private val propertyFake: FakePropertyRemoteDataSource,
    private val searchFake: FakeSearchRemoteDataSource
) {

    /**
     * Resets all chaos controllers and fakes to their initial success state.
     * Prevents cross-test contamination in integration tests.
     */
    fun reset() {
        networkChaos.reset()
        (networkClient as? ChaosNetworkClient)?.reset()
        authFake.setNextBehavior(AuthBehavior.Authenticated)
        propertyFake.setNextBehavior(DatabaseBehavior.Success)
        searchFake.setNextBehavior(DatabaseBehavior.Success)
        
        // Reset resource states
        resourceController.isAppVisible = true
        resourceController.isInteractive = true
        resourceController.memoryPressure = ChaosResourceController.MemoryPressure.Normal
    }

    /**
     * Simulates a total network blackout.
     */
    fun networkOffline() {
        networkChaos.script(NetworkBehavior.Offline)
    }

    /**
     * Simulates transient timeouts that should be recovered by production retries.
     */
    fun networkTimeoutRetry() {
        networkChaos.script(
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout,
            NetworkBehavior.Success
        )
    }

    /**
     * Simulates an expired session requiring a token refresh.
     */
    fun authExpired() {
        authFake.setNextBehavior(AuthBehavior.TokenExpired)
    }

    /**
     * Simulates a database lock contention scenario.
     */
    fun databaseLocked() {
        propertyFake.setNextBehavior(DatabaseBehavior.Locked)
    }

    /**
     * Simulates the "Ghost Write" scenario: Server creates the record but client times out.
     */
    fun serverSuccessClientTimeout() {
        networkChaos.script(NetworkBehavior.ServerSuccessClientTimeout)
    }

    /**
     * Simulates a backend service outage (503).
     */
    fun serverUnavailable() {
        networkChaos.script(NetworkBehavior.HttpError(503))
    }

    /**
     * Simulates a search reordering scenario where a subsequent search
     * finishes before an earlier, slower one.
     */
    fun outOfOrderSearchResponses() {
        networkChaos.script(
            NetworkBehavior.OutOfOrderResponse,
            NetworkBehavior.Success
        )
    }

    /**
     * 🏎️ ADVANCED: Simulates two logical requests attempting to refresh tokens.
     * Uses sequential semantic reordering (OutOfOrderResponse then Success) to model
     * the scenario where a subsequent request's completion logic is processed while
     * the first is still pending, forcing a conflict state.
     */
    fun concurrentTokenRefresh() {
        // Script the network to hold the first request (which will trigger refresh)
        // and allow the second one to proceed immediately to a conflict state.
        networkChaos.script(
            NetworkBehavior.OutOfOrderResponse, // Request 1 held
            NetworkBehavior.Success             // Request 2 completes (triggering race)
        )
        // Configure Auth fake to surface the concurrent refresh error
        authFake.setNextBehavior(AuthBehavior.MultipleRefreshRequests)
    }
}
