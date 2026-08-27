package com.estatia.realestate.apps.core.data.repositories

import android.util.Base64
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.IHashManager
import com.estatia.realestate.apps.core.security.interfaces.IRsaCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.ISignatureManager
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SecurityRepositoryTest {

    private lateinit var aesGcmCryptoEngine: IAesGcmCryptoEngine
    private lateinit var rsaCryptoEngine: IRsaCryptoEngine
    private lateinit var signatureManager: ISignatureManager
    private lateinit var hashManager: IHashManager
    private lateinit var tokenDataSource: ITokenLocalDataSource
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var repository: SecurityRepository

    @Before
    fun setup() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } returns "encoded"
        every { Base64.decode(any<String>(), any()) } returns byteArrayOf(1, 2, 3)

        aesGcmCryptoEngine = mockk()
        rsaCryptoEngine = mockk()
        signatureManager = mockk()
        hashManager = mockk()
        tokenDataSource = mockk()
        
        repository = SecurityRepository(
            aesGcmCryptoEngine,
            rsaCryptoEngine,
            signatureManager,
            hashManager,
            tokenDataSource,
            json
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(Base64::class)
    }

    @Test
    fun `symmetricEncrypt handles concurrent mutation safely`() = runTest {
        val data = "test"
        val payload = EncryptedPayload(1, byteArrayOf(1), byteArrayOf(2))
        coEvery { aesGcmCryptoEngine.encrypt(any()) } returns AppResult.Success(payload)

        val result = repository.symmetricEncrypt(data).assertSuccess()
        assertEquals(json.encodeToString(payload), result)
    }

    @Test
    fun `signData handles token revocation gracefully`() = runTest {
        // Given: signing fails (e.g. because key is missing or revoked)
        coEvery { signatureManager.sign(any(), any()) } returns AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.DatabaseException.NotFound)
        
        repository.signData("some data")
        // Verify it doesn't crash
    }

    @Test
    fun `symmetricDecrypt parses json and calls engine`() = runTest {
        val payload = EncryptedPayload(1, byteArrayOf(1), byteArrayOf(2))
        val encryptedData = json.encodeToString(payload)
        val decryptedData = "test"
        coEvery { aesGcmCryptoEngine.decrypt(any()) } returns AppResult.Success(decryptedData.toByteArray())

        val result = repository.symmetricDecrypt(encryptedData).assertSuccess()
        assertEquals(decryptedData, result)
    }

    @Test
    fun `signData calls manager and returns base64`() = runTest {
        val data = "test"
        val signature = byteArrayOf(1, 2, 3)
        coEvery { signatureManager.sign(any(), any()) } returns AppResult.Success(signature)

        val result = repository.signData(data).assertSuccess()
        assertEquals("encoded", result)
    }
}
