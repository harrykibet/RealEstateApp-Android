package com.estatia.realestate.apps.core.testing_network.chaos

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.core.RetryConfig
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verifies that the [ChaosNetworkClient] handles "Semantic Chaos" (Reordering, Truncation)
 * faithfully according to operational contracts.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SemanticNetworkChaosTest {

    private lateinit var networkChaos: NetworkChaosController
    private lateinit var exceptionMapper: IExceptionMapper
    private lateinit var retryPolicy: IRetryPolicy
    private lateinit var client: ChaosNetworkClient

    @Before
    fun setup() {
        networkChaos = NetworkChaosController()
        exceptionMapper = mockk(relaxed = true)

        // Use a simple fake retry policy to execute the block once
        retryPolicy = object : IRetryPolicy {
            override suspend fun <T> execute(config: RetryConfig?, block: suspend () -> T): T = block()
        }

        client = ChaosNetworkClient(
            networkChaos = networkChaos,
            exceptionMapper = exceptionMapper,
            retryPolicy = retryPolicy
        )
    }

    @Test
    fun `OutOfOrderResponse parks the first request until the second one completes`() = runTest {
        // 🧪 Script: First request is held (OutOfOrder), second is normal (Success)
        networkChaos.script(NetworkBehavior.OutOfOrderResponse, NetworkBehavior.Success)

        val completionOrder = mutableListOf<String>()

        // Launch Request 1 (will be held)
        val job1 = launch {
            client.execute(null) { "req1" }
            completionOrder.add("req1")
        }

        // Ensure Request 1 reached the "held" state
        yield()
        assertTrue("Request 1 should still be active", job1.isActive)
        assertTrue("No request should have completed yet", completionOrder.isEmpty())

        // Execute Request 2 (will complete and release Request 1)
        client.execute(null) { "req2" }
        completionOrder.add("req2")

        // Now Request 1 should be released
        yield()
        job1.join()

        // Assert Semantic Reordering: req2 finished before req1
        assertEquals(listOf("req2", "req1"), completionOrder)
    }

    @Test
    fun `PartialResponse truncates list payload semantically`() = runTest {
        // 🧪 Script: Success but partial
        networkChaos.script(NetworkBehavior.PartialResponse)

        val fullData = listOf("item1", "item2", "item3", "item4")
        
        val result = client.execute(null) { fullData }

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals("List should be truncated by half", 2, data.size)
        assertEquals(listOf("item1", "item2"), data)
    }

    @Test
    fun `PartialResponse truncates string payload semantically`() = runTest {
        // 🧪 Script: Success but partial
        networkChaos.script(NetworkBehavior.PartialResponse)

        val fullString = "Hello World"
        
        val result = client.execute(null) { fullString }

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals("String should be truncated by half", "Hello", data)
    }
}
