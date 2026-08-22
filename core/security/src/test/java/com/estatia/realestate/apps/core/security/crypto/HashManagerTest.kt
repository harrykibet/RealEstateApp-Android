package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.security.core.CryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import com.estatia.realestate.apps.core.common.interfaces.ILogger
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
    fun `hash produces consistent output`() = runTest {
        val data = "test_data".toByteArray()
        val result1 = hashManager.hash(data)
        val result2 = hashManager.hash(data)

        assertTrue(result1 is AppResult.Success)
        assertTrue(result2 is AppResult.Success)
        assertArrayEquals((result1 as AppResult.Success).data, (result2 as AppResult.Success).data)
    }

    @Test
    fun `hashWithSalt produces different output for different salts`() = runTest {
        val data = "test_data".toByteArray()
        val salt1 = hashManager.generateSalt(16)
        val salt2 = hashManager.generateSalt(16)

        val result1 = hashManager.hashWithSalt(data, salt1)
        val result2 = hashManager.hashWithSalt(data, salt2)

        assertTrue(result1 is AppResult.Success)
        assertTrue(result2 is AppResult.Success)
        
        val hash1 = (result1 as AppResult.Success).data
        val hash2 = (result2 as AppResult.Success).data
        
        var equal = true
        if (hash1.size == hash2.size) {
            for (i in hash1.indices) {
                if (hash1[i] != hash2[i]) {
                    equal = false
                    break
                }
            }
        } else {
            equal = false
        }
        assertTrue("Hashes with different salts should be different", !equal)
    }

    @Test
    fun `hmacSha256 produces valid hmac`() = runTest {
        val data = "message".toByteArray()
        val key = "secret_key".toByteArray()
        val result = hashManager.hmacSha256(data, key)

        assertTrue(result is AppResult.Success)
        assertEquals(32, (result as AppResult.Success).data.size)
    }
}
