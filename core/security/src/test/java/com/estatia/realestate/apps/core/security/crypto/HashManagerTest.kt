package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.security.MessageDigest

class HashManagerTest {

    private lateinit var cryptoExecutor: ICryptoExecutor
    private lateinit var hashManager: HashManager

    @Before
    fun setup() {
        cryptoExecutor = mockk()
        // Mocking the executor to just run the block since we already tested the executor itself
        coEvery { cryptoExecutor.execute<Any>(any(), any()) } coAnswers {
            val block = secondArg<suspend () -> Any>()
            try {
                AppResult.Success(block())
            } catch (e: Exception) {
                AppResult.Error(SecurityException.HashGenerationFailed(e))
            }
        }
        hashManager = HashManager(cryptoExecutor)
    }

    @Test
    fun `hash produces correct SHA-256 value`() = runTest {
        val data = "test-data".toByteArray()
        val expected = MessageDigest.getInstance("SHA-256").digest(data)

        val result = hashManager.hash(data)

        assert(result is AppResult.Success)
        assertArrayEquals(expected, (result as AppResult.Success).data)
    }

    @Test
    fun `hashWithSalt produces correct salted SHA-256 value`() = runTest {
        val data = "test-data".toByteArray()
        val salt = "salt".toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val expected = digest.digest(data)

        val result = hashManager.hashWithSalt(data, salt)

        assert(result is AppResult.Success)
        assertArrayEquals(expected, (result as AppResult.Success).data)
    }

    @Test
    fun `generateSalt produces random bytes`() {
        val salt1 = hashManager.generateSalt(16)
        val salt2 = hashManager.generateSalt(16)

        assertEquals(16, salt1.size)
        assertEquals(16, salt2.size)
        // Highly unlikely they are equal
        assertNotEquals(salt1.joinToString(), salt2.joinToString())
    }

    @Test
    fun `hmacSha256 produces valid HMAC`() = runTest {
        val data = "message".toByteArray()
        val key = "secret-key".toByteArray()

        val result = hashManager.hmacSha256(data, key)

        assert(result is AppResult.Success)
        assertEquals(32, (result as AppResult.Success).data.size) // SHA-256 HMAC is 32 bytes
    }
}
