package com.estatia.realestate.apps.core.security.core

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CryptoExecutorTest {

    private lateinit var translator: ISecurityExceptionTranslator
    private lateinit var logger: ILogger
    private lateinit var executor: CryptoExecutor

    @Before
    fun setup() {
        translator = mockk()
        logger = mockk(relaxed = true)
        executor = CryptoExecutor(translator, logger)
    }

    @Test
    fun `execute success returns Success result`() = runTest {
        val expected = "result"
        val result = executor.execute(SecurityException.KeyGenerationFailed) {
            expected
        }
        assert(result is AppResult.Success)
        assertEquals(expected, (result as AppResult.Success).data)
    }

    @Test
    fun `execute failure calls translator and logger and returns Error result`() = runTest {
        val originalException = RuntimeException("error")
        val translatedException = SecurityException.KeyRetrievalFailed
        every { translator.translate(originalException, any()) } returns translatedException

        val result = executor.execute(SecurityException.KeyGenerationFailed) {
            throw originalException
        }

        assert(result is AppResult.Error)
        assertEquals(translatedException, (result as AppResult.Error).exception)
        verify { logger.e(tag = "CryptoExecutor", message = any(), throwable = originalException) }
    }
}
