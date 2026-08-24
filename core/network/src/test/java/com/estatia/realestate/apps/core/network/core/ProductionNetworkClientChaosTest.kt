package com.estatia.realestate.apps.core.network.core

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper
import com.estatia.realestate.apps.core.network.interfaces.IRetryPolicy
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ProductionNetworkClientChaosTest {

    private lateinit var retryPolicy: IRetryPolicy
    private lateinit var exceptionMapper: IExceptionMapper
    private lateinit var logger: ILogger
    private lateinit var client: ProductionNetworkClient

    @Before
    fun setup() {
        retryPolicy = mockk()
        exceptionMapper = mockk()
        logger = mockk(relaxed = true)
        client = ProductionNetworkClient(retryPolicy, exceptionMapper, logger)
    }

    @Test
    fun `execute returns success when retry policy succeeds`() = runTest {
        val expected = "success"
        coEvery { retryPolicy.execute<String>(any(), any()) } returns expected

        val result = client.execute(null) { "op" }

        assertTrue(result is AppResult.Success)
        assert((result as AppResult.Success).data == expected)
    }

    @Test
    fun `execute returns mapped error when retry policy fails`() = runTest {
        val original = IOException("Connection reset")
        val mapped = NetworkException.ConnectionFailed
        
        coEvery { retryPolicy.execute<String>(any(), any()) } throws original
        every { exceptionMapper.map(original) } returns mapped

        val result = client.execute(null) { "op" }

        assertTrue(result is AppResult.Error)
        assert((result as AppResult.Error).exception == mapped)
        verify { logger.e(message = any(), throwable = mapped) }
    }
}
