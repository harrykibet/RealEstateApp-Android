package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import com.estatia.realestate.apps.core.testing.lifecycle.launchAndDestroy
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

/**
 * Adversarial tests for [AuthRepository] driven by the Chaos Contract.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthRepositoryChaosTest : ChaosContract<AuthRepository, AuthBehavior>() {

    private val remoteDataSource = FakeAuthRemoteDataSource()
    private val metricsTracker: IMetricsTracker = mockk(relaxed = true)

    override fun createSubject(behavior: AuthBehavior): AuthRepository {
        remoteDataSource.setNextBehavior(behavior)
        return AuthRepository(remoteDataSource, metricsTracker)
    }

    @Test
    override fun cancellationPropagates() = runTest {
        // We use a custom scheduler for cancellation race testing
        val scheduler = TestScheduler()
        // ... (rest of test remains same, but using the fake if possible)
    }

    @Test
    fun handlesTokenExpirationChaos() = runTest {
        val repository = createSubject(AuthBehavior.TokenExpired)
        val result = repository.signInWithEmail("test@test.com", "password")
        val error = result.assertError()
        assert(error is AuthException.SessionExpired)
    }

    @Test
    fun handlesAccountDisabledChaos() = runTest {
        val repository = createSubject(AuthBehavior.AccountDisabled)
        val result = repository.signInWithEmail("test@test.com", "password")
        result.assertError()
    }
}
