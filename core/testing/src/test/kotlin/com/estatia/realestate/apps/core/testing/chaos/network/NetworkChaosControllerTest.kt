package com.estatia.realestate.apps.core.testing.chaos.network

import com.estatia.realestate.apps.core.testing.chaos.server.ServerScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkChaosControllerTest {

    private val controller = NetworkChaosController()

    @Test
    fun `script and executeNext follows sequence`() = runTest {
        controller.script(NetworkBehavior.Offline, NetworkBehavior.Timeout)
        
        try {
            controller.executeNext()
            fail("Should have thrown Offline")
        } catch (e: IOException) {
            assertEquals("No network connectivity (Chaos)", e.message)
        }

        try {
            controller.executeNext()
            fail("Should have thrown Timeout")
        } catch (e: SocketTimeoutException) {
            assertEquals("Connection timed out (Chaos)", e.message)
        }

        // Subsequent calls return success
        controller.executeNext()
    }

    @Test
    fun `setServerScenario injects server error`() = runTest {
        controller.setServerScenario(ServerScenario.MalformedJson)
        
        try {
            controller.executeNext()
            fail("Should have thrown MalformedJson")
        } catch (e: IOException) {
            assertEquals("Malformed JSON (Chaos)", e.message)
        }
    }

    @Test
    fun `delay behavior works`() = runTest {
        controller.script(NetworkBehavior.Delay(100.milliseconds))
        val startTime = testScheduler.currentTime
        controller.executeNext()
        val endTime = testScheduler.currentTime
        assertEquals(100L, endTime - startTime)
    }

    @Test
    fun `HttpError throws with status code`() = runTest {
        controller.script(NetworkBehavior.HttpError(404))
        try {
            controller.executeNext()
            fail("Should have thrown HttpError")
        } catch (e: IOException) {
            assertEquals("HTTP 404 (Chaos)", e.message)
        }
    }
}
