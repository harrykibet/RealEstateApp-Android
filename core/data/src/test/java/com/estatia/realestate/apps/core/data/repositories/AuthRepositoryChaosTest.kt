package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.db_entities.NetworkUserEntity
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.estatia.realestate.apps.core.testing.chaos.auth.AuthBehavior
import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

/**
 * Adversarial tests for [AuthRepository] driven by the Chaos Contract.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthRepositoryChaosTest : ChaosContract<AuthRepository, AuthBehavior>() {

    private val remoteDataSource: IAuthRemoteDataSource = mockk()
    private val metricsTracker: IMetricsTracker = mockk(relaxed = true)

    override fun createSubject(behavior: AuthBehavior): AuthRepository {
        return AuthRepository(remoteDataSource, metricsTracker).also {
            applyBehavior(behavior)
        }
    }

    private fun applyBehavior(behavior: AuthBehavior) {
        when (behavior) {
            AuthBehavior.TokenExpired -> {
                coEvery { remoteDataSource.signInWithEmail(any(), any()) } returns 
                    AppResult.Error(AuthException.SessionExpired)
            }
            AuthBehavior.AccountDisabled -> {
                coEvery { remoteDataSource.signInWithEmail(any(), any()) } returns 
                    AppResult.Error(AuthException.UserNotFound) // Assuming mapping
            }
            else -> Unit
        }
    }

    @Test
    override fun cancellationPropagates() = runTest {
        val repository = createSubject(AuthBehavior.Authenticated)
        
        coEvery { remoteDataSource.signInWithEmail(any(), any()) } coAnswers {
            delay(5.seconds)
            AppResult.Success(NetworkUserEntity("id", "name", null, null, null, true))
        }

        val job = async {
            repository.signInWithEmail("test@test.com", "password")
        }
        
        delay(1.seconds)
        job.cancelAndJoin()
        
        assert(job.isCancelled)
    }

    @Test
    fun handlesTokenExpirationChaos() = runTest {
        val repository = createSubject(AuthBehavior.TokenExpired)
        val result = repository.signInWithEmail("test@test.com", "password")
        assert(result is AppResult.Error && result.exception is AuthException.SessionExpired)
    }

    @Test
    fun handlesAccountDisabledChaos() = runTest {
        val repository = createSubject(AuthBehavior.AccountDisabled)
        val result = repository.signInWithEmail("test@test.com", "password")
        assert(result is AppResult.Error)
    }
}
