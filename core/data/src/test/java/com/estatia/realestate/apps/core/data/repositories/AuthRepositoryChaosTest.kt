package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.coroutine.TestScheduler
import com.estatia.realestate.apps.core.testing_network.chaos.contracts.NetworkChaosContract
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeAuthRemoteDataSource
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Adversarial tests for [AuthRepository] driven by the Chaos Contract.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthRepositoryChaosTest : NetworkChaosContract<AuthRepository, AuthBehavior>() {

    private val remoteDataSource = FakeAuthRemoteDataSource()
    private val metricsTracker: IMetricsTracker = mockk(relaxed = true)

    override val successBehavior = AuthBehavior.Authenticated
    override val failureBehavior = AuthBehavior.AccountDisabled
    override val timeoutBehavior = AuthBehavior.RefreshTimeout

    override fun createSubject(behavior: AuthBehavior): AuthRepository {
        remoteDataSource.setNextBehavior(behavior)
        return AuthRepository(remoteDataSource, metricsTracker)
    }

    override suspend fun performOperation(subject: AuthRepository): Any? {
        return subject.signInWithEmail("test@test.com", "password")
    }

    @Test
    override fun cancellationPropagates() = runTest {
        val scheduler = TestScheduler()
        
        // Use a manual fake for this test to control suspension safely
        val hangingRemote = object : IAuthRemoteDataSource by FakeAuthRemoteDataSource() {
            override suspend fun signInWithEmail(email: String, password: String): AppResult<NetworkUserEntity> {
                scheduler.release("auth_op")
                awaitCancellation()
            }
        }
        
        val repository = AuthRepository(hangingRemote, metricsTracker)

        // Use UnconfinedTestDispatcher to ensure the coroutine advances 
        // to the suspension point immediately and releases the scheduler.
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.signInWithEmail("test@test.com", "password")
        }
        
        scheduler.awaitPoint("auth_op")
        job.cancel()
        job.join()
    }

    @Test
    fun handlesTokenExpirationChaos() = runTest {
        val repository = createSubject(AuthBehavior.TokenExpired)
        val result = repository.signInWithEmail("test@test.com", "password")
        val error = result.assertError()
        assert(error is AuthException.SessionExpired)
    }
}
