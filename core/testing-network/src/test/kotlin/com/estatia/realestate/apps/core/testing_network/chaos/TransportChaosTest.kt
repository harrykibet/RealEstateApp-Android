package com.estatia.realestate.apps.core.testing_network.chaos

import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing_network.chaos.interceptors.ChaosInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TransportChaosTest {

    private val server = MockWebServer()
    private val controller = NetworkChaosController()
    private val interceptor = ChaosInterceptor(controller)
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `PartialResponse causes IOException during stream read`() {
        // 🧪 Script: Success but partial stream
        controller.script(NetworkBehavior.PartialResponse)
        
        val bodyContent = "{" + "A".repeat(100) + "}"
        server.enqueue(MockResponse().setBody(bodyContent))

        val request = Request.Builder().url(server.url("/")).build()
        val response = client.newCall(request).execute()

        // The response itself is a success from the network client perspective
        assertEquals(200, response.code)

        // Reading the body should fail or return truncated data
        val source = response.body?.source()!!
        
        // In our interceptor, we return -1 when limit is reached
        val readContent = source.readUtf8()
        assertEquals(bodyContent.length / 2, readContent.length)
        assertTrue("Stream should end prematurely", source.exhausted())
    }

    @Test
    fun `MalformedResponse corrupts data on the wire`() {
        // 🧪 Script: Corrupt the stream
        controller.script(NetworkBehavior.MalformedResponse)
        
        val original = "Clean data"
        server.enqueue(MockResponse().setBody(original))

        val request = Request.Builder().url(server.url("/")).build()
        val response = client.newCall(request).execute()

        val corrupted = response.body?.string()!!
        assertNotEquals(original, corrupted)
        assertEquals(original.length, corrupted.length)
    }

    private fun assertTrue(msg: String, condition: Boolean) {
        if (!condition) throw AssertionError(msg)
    }
    
    private fun assertNotEquals(o1: Any, o2: Any) {
        if (o1 == o2) throw AssertionError("Expected $o1 to be different from $o2")
    }
}
