package com.estatia.realestate.apps.core.testing_network.chaos.interceptors

import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class ChaosInterceptorTest {

    private lateinit var controller: NetworkChaosController
    private lateinit var interceptor: ChaosInterceptor

    @Before
    fun setup() {
        controller = NetworkChaosController()
        interceptor = ChaosInterceptor(controller)
    }

    @Test
    fun `MalformedResponse corrupts data byte-for-byte`() {
        val originalText = "Estatia Production Data"
        val originalBytes = originalText.toByteArray()
        
        controller.script(NetworkBehavior.MalformedResponse)

        val request = Request.Builder().url("http://test.com").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(originalText.toResponseBody("text/plain".toMediaType()))
            .build()

        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(any()) } returns response

        val interceptedResponse = interceptor.intercept(chain)
        val body = interceptedResponse.body!!
        
        val corruptedSource = body.source()
        val corruptedBytes = corruptedSource.readByteArray()

        assertEquals("Content length should remain same", originalBytes.size.toLong(), body.contentLength())
        assertNotEquals("Bytes must be corrupted", originalBytes.toList(), corruptedBytes.toList())
        
        // Specific verification of our XOR logic: flip the first byte of the first read
        val expectedFirstByte = (originalBytes[0].toInt() xor 0xFF).toByte()
        assertEquals("First byte should be XORed with 0xFF", expectedFirstByte, corruptedBytes[0])
        
        // Remaining bytes should match original if our "remaining" buffering works
        if (originalBytes.size > 1) {
            assertEquals("Subsequent bytes should be preserved", originalBytes[1], corruptedBytes[1])
        }
    }
}
