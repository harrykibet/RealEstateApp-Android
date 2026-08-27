package com.estatia.realestate.apps.core.testing_network.chaos

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyBehavior
import com.estatia.realestate.apps.core.testing.chaos.concurrency.ConcurrencyChaosController
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleBehavior
import com.estatia.realestate.apps.core.testing.chaos.lifecycle.LifecycleChaosController
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ChaosNetworkClient].
 */
class ChaosNetworkClientTest {

    private lateinit var networkChaos: NetworkChaosController
    private lateinit var concurrencyChaos: ConcurrencyChaosController
    private lateinit var lifecycleChaos: LifecycleChaosController
    private lateinit var exceptionMapper: IExceptionMapper
    private lateinit var retryPolicy: IRetryPolicy
    private lateinit var client: ChaosNetworkClient

    @Before
    fun setup() {
        networkChaos = NetworkChaosController()
        concurrencyChaos = ConcurrencyChaosController()
        lifecycleChaos = LifecycleChaosController()
        exceptionMapper = mockk()
        retryPolicy = mockk()

        // Mock retry policy to simply execute the block once for baseline unit testing.
        // In integration tests, the real ExponentialRetryPolicy should be used to drive retries.
        io.mockk.coEvery { retryPolicy.execute<Any?>(any(), any()) } coAnswers {
            val block = secondArg<suspend () -> Any?>()
            block()
        }

        client = ChaosNetworkClient(
            networkChaos = networkChaos,
            concurrencyChaos = concurrencyChaos,
            lifecycleChaos = lifecycleChaos,
            exceptionMapper = exceptionMapper,
            retryPolicy = retryPolicy
        )
    }

    @Test
    fun `execute returns success when no chaos is injected`() = runTest {
        // Arrange
        val expectedData = "test-data"

        // Act
        val result = client.execute(null) { expectedData }

        // Assert
        assertTrue(result is AppResult.Success)
        assertEquals(expectedData, (result as AppResult.Success).data)
    }

    @Test
    fun `execute maps network chaos failures via exception mapper`() = runTest {
        // Arrange
        networkChaos.script(NetworkBehavior.Offline)
        val expectedException = NetworkException.Unknown(Exception("Offline"))
        every { exceptionMapper.map(any()) } returns expectedException

        // Act
        val result = client.execute(null) { "data" }

        // Assert
        assertTrue(result is AppResult.Error)
        assertEquals(expectedException, (result as AppResult.Error).exception)
    }

    @Test
    fun `execute maps lifecycle chaos failures via exception mapper`() = runTest {
        // Arrange
        lifecycleChaos.setNextBehavior(LifecycleBehavior.ProcessDeath)
        val expectedException = NetworkException.Unknown(Exception("Lifecycle Error"))
        every { exceptionMapper.map(any()) } returns expectedException

        // Act
        val result = client.execute(null) { "data" }

        // Assert
        assertTrue(result is AppResult.Error)
        assertEquals(expectedException, (result as AppResult.Error).exception)
    }

    @Test
    fun `execute maps concurrency chaos failures via exception mapper`() = runTest {
        // Arrange
        concurrencyChaos.setNextBehavior(ConcurrencyBehavior.DuplicateRequest)
        val expectedException = NetworkException.Unknown(Exception("Concurrency Error"))
        every { exceptionMapper.map(any()) } returns expectedException

        // Act & Assert
        // First call - unique points "network_pre_execute" and "network_post_execute" are hit once.
        val result1 = client.execute(null) { "data1" }
        assertTrue(result1 is AppResult.Success)
        
        // Second call - "network_pre_execute" point is now reached for the second time, triggering chaos.
        val result2 = client.execute(null) { "data2" }

        assertTrue(result2 is AppResult.Error)
        assertEquals(expectedException, (result2 as AppResult.Error).exception)
    }
}
