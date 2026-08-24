package com.estatia.realestate.apps.core.security.core

import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import com.estatia.realestate.apps.core.testing.chaos.models.TestFailure
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
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        logger = mockk(relaxed = true)
        executor = CryptoExecutor(translator, metricsTracker, logger)
    }

    @Test
    fun `execute success returns Success result using platform assertions`() = runTest {
        val expected = "result"
        val result = executor.execute(SecurityException.KeyGenerationFailed) {
            expected
        }
        
        val data = result.assertSuccess()
        assertEquals(expected, data)
    }

    @Test
    fun `execute failure handles unexpected chaos gracefully`() = runTest {
        // 🧪 Adversarial Behavior: Corrupted Data during crypto
        println("Testing behavior: ${TestFailure.CorruptedData}")
        
        val originalException = RuntimeException("Corrupted")
        val translatedException = SecurityException.KeyRetrievalFailed
        every { translator.translate(originalException, any()) } returns translatedException

        val result = executor.execute(SecurityException.KeyGenerationFailed) {
            throw originalException
        }

        val err = result.assertError()
        assertEquals(translatedException, err)
        verify { logger.e(tag = "CryptoExecutor", message = any(), throwable = originalException) }
    }
}
