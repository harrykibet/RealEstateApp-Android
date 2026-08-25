package com.estatia.realestate.apps.feature.auth.viewModels

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.RemoteServiceException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.testing.assertions.assertState
import com.estatia.realestate.apps.core.testing.fixtures.AuthFixtures
import com.estatia.realestate.apps.feature.auth.state.AuthState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = LoginViewModel(authRepository, metricsTracker)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with email success updates state to authenticated`() = runTest {
        // Given
        val user = AuthFixtures.default()
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns AppResult.Success(user)

        viewModel.authState.test {
            // Initial state
            assertEquals(AuthState.Idle, awaitItem())

            // When
            viewModel.loginWithEmail("test@example.com", "password")

            // Then
            assertEquals(AuthState.Loading, awaitItem())
            
            // Using new platform assertions
            viewModel.authState.assertState { 
                this is AuthState.Authenticated && this.user == user 
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login with unverified email updates state to verification required`() = runTest {
        // Given
        val user = AuthFixtures.build(isEmailVerified = false)
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns AppResult.Success(user)

        viewModel.authState.test {
            awaitItem() // Idle
            viewModel.loginWithEmail("test@example.com", "password")
            awaitItem() // Loading
            
            viewModel.authState.assertState { 
                this is AuthState.EmailVerificationRequired && email == "test@example.com" 
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login failure updates error state`() = runTest {
        // Given
        val exception = RemoteServiceException.Unknown(Exception("Login failed"))
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns AppResult.Error(exception)

        viewModel.authState.test {
            awaitItem() // Idle
            viewModel.loginWithEmail("test@example.com", "password")
            awaitItem() // Loading
            
            viewModel.authState.assertState { 
                this is AuthState.Error && message.contains("Unknown") 
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
