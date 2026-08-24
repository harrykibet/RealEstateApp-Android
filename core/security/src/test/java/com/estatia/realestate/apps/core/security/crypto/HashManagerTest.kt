package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.security.core.CryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HashManagerTest {

    private lateinit var hashManager: HashManager
    private lateinit var cryptoExecutor: CryptoExecutor

    @Before
    fun setup() {
        val translator = mockk<ISecurityExceptionTranslator>()
        val logger = mockk<ILogger>(relaxed = true)
        cryptoExecutor = CryptoExecutor(translator, logger)
        hashManager = HashManager(cryptoExecutor)
    }

    @Test
    fun `hash produces consistent output using platform assertions`() = runTest {
        val data = "test_data".toByteArray()
        
        val result1 = hashManager.hash(data).assertSuccess()
        val result2 = hashManager.hash(data).assertSuccess()

        assertArrayEquals(result1, result2)
    }

    @Test
    fun `hashWithSalt produces different output for different salts`() = runTest {
        val data = "test_data".toByteArray()
        val salt1 = hashManager.generateSalt(16)
        val salt2 = hashManager.generateSalt(16)

        val hash1 = hashManager.hashWithSalt(data, salt1).assertSuccess()
        val hash2 = hashManager.hashWithSalt(data, salt2).assertSuccess()

        assertTrue("Hashes with different salts should be different", !hash1.contentEquals(hash2))
    }

    @Test
    fun `hmacSha256 produces valid 32-byte hmac`() = runTest {
        val data = "message".toByteArray()
        val key = "secret_key".toByteArray()
        val hmac = hashManager.hmacSha256(data, key).assertSuccess()

        assertEquals(32, hmac.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `generateSalt throws on invalid length`() {
        hashManager.generateSalt(-1)
    }
}
