package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.time.ChaosClock
import com.estatia.realestate.apps.core.testing.chaos.time.TimeBehavior
import com.estatia.realestate.apps.core.testing.clock.TestClock
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ExponentialRetryPolicyChaosTest {

    private lateinit var exceptionMapper: IExceptionMapper
    private lateinit var retryPolicy: ExponentialRetryPolicy
    private val testClock = TestClock(0L)
    private val chaosClock = ChaosClock(testClock)

    @Before
    fun setup() {
        exceptionMapper = mockk()
        retryPolicy = ExponentialRetryPolicy(exceptionMapper, clock = { chaosClock.currentTimeMillis() })
    }

    @Test
    fun `retry policy succeeds after transient failures`() = runTest {
        val config = RetryConfig(
            name = "test",
            maxAttempts = 3,
            initialDelayMs = 100,
            maxDelayMs = 1000,
            multiplier = 2.0
        )

        val failures = listOf(
            NetworkBehavior.Timeout,
            NetworkBehavior.Timeout,
            NetworkBehavior.Success
        )

        var callCount = 0
        every { exceptionMapper.map(any()) } returns NetworkException.Timeout

        val result = retryPolicy.execute(config) {
            when (failures[callCount++]) {
                NetworkBehavior.Timeout -> throw IOException("Timeout")
                else -> "Success"
            }
        }

        assertEquals("Success", result)
        assertEquals(3, callCount)
    }

    @Test(expected = NetworkException.Timeout::class)
    fun `retry policy exhausts attempts and throws last exception`() = runTest {
        val config = RetryConfig(name = "test", maxAttempts = 2, initialDelayMs = 10, maxDelayMs = 100, multiplier = 2.0)
        every { exceptionMapper.map(any()) } returns NetworkException.Timeout

        retryPolicy.execute(config) {
            throw IOException("Permanent Failure")
        }
    }

    @Test(expected = NetworkException.Timeout::class)
    fun `retry policy respects max total duration`() = runTest {
        val config = RetryConfig(
            name = "test",
            maxAttempts = 10,
            initialDelayMs = 100,
            maxDelayMs = 1000,
            multiplier = 2.0,
            maxTotalDurationMs = 500
        )
        every { exceptionMapper.map(any()) } returns NetworkException.Timeout

        retryPolicy.execute(config) {
            testClock.advanceBy(600) // Advance clock mid-operation
            throw IOException("Timeout")
        }
    }

    @Test
    fun `retry policy handles server success but client timeout chaos`() = runTest {
        // 🧪 Chaos Scenario: Server processed but client timed out
        val config = RetryConfig(name = "test", maxAttempts = 2, initialDelayMs = 10, maxDelayMs = 100, multiplier = 2.0)
        every { exceptionMapper.map(any()) } returns NetworkException.Timeout
        
        var attempts = 0
        val result = retryPolicy.execute(config) {
            attempts++
            if (attempts == 1) throw IOException("SocketTimeout after side-effect")
            "Recovered"
        }
        
        assertEquals("Recovered", result)
    }

    @Test
    fun `retry policy handles clock skew chaos safely`() = runTest {
        // 🧪 Chaos Scenario: Clock Skew
        chaosClock.setNextBehavior(TimeBehavior.ClockSkew)
        
        val config = RetryConfig(name = "test", maxAttempts = 2, initialDelayMs = 10, maxDelayMs = 100, multiplier = 2.0)
        every { exceptionMapper.map(any()) } returns NetworkException.Timeout
        
        val result = retryPolicy.execute(config) { "Recovered" }
        assertEquals("Recovered", result)
    }
}
