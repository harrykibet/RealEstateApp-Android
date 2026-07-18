package com.estatia.realestate.apps.core.security

import android.util.Base64
import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import javax.crypto.SecretKey
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalCryptoManagerTest {

    private lateinit var cryptoManager: LocalCryptoManager
    private lateinit var logger: LoggerInterface
    private lateinit var keyStore: KeyStore

    @BeforeAll
    fun setup() {
        logger = mockk(relaxed = true)
        cryptoManager = spyk(LocalCryptoManager(logger), recordPrivateCalls = true)

        keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    @Test
    fun `rotateAesKey should succeed`() = runBlocking {
        every { keyStore.deleteEntry(any()) } just Runs
        every { cryptoManager invokeNoArgs "generateAesKey" } just Awaits

        val result = cryptoManager.rotateAesKey()

        assertTrue(result is AppResult.Success)
        verify { keyStore.deleteEntry("secure_app_key") }
        verify { cryptoManager invokeNoArgs "generateAesKey" }
    }

    @Test
    fun `aesEncrypt should encrypt successfully`() = runBlocking {
        val plaintext = "HelloWorld".toByteArray()
        val secretKey = mockk<SecretKey>()
        val cipher = mockk<Cipher>()

        every { cryptoManager invokeNoArgs "getAesKey" } returns secretKey
        every { Cipher.getInstance(any()) } returns cipher
        every { cipher.init(Cipher.ENCRYPT_MODE, any<SecretKey>()) } just Runs
        every { cipher.iv } returns ByteArray(12) { 1 }
        every { cipher.doFinal(any<ByteArray>()) } returns ByteArray(16) { 2 }

        val result = cryptoManager.aesEncrypt(plaintext)

        assertTrue(result is AppResult.Success)
        assertEquals(28, (result as AppResult.Success).data.size) // 12 IV + 16 Data
    }

    @Test
    fun `aesDecrypt should decrypt successfully`() = runBlocking {
        val encryptedData = ByteArray(28) { if (it < 12) 1 else 2 }
        val secretKey = mockk<SecretKey>()
        val cipher = mockk<Cipher>()

        every { cryptoManager invokeNoArgs "getAesKey" } returns secretKey
        every { Cipher.getInstance(any()) } returns cipher
        every { cipher.init(Cipher.DECRYPT_MODE, any<SecretKey>(), any<GCMParameterSpec>()) } just Runs
        every { cipher.doFinal(any<ByteArray>(), any(), any()) } returns "HelloWorld".toByteArray()

        val result = cryptoManager.aesDecrypt(encryptedData)

        assertTrue(result is AppResult.Success)
        assertEquals("HelloWorld", String((result as AppResult.Success).data))
    }

    @Test
    fun `rsaEncrypt should encrypt successfully`() = runBlocking {
        val plaintext = "SecureData".toByteArray()
        val publicKey = mockk<PublicKey>()
        val cipher = mockk<Cipher>()

        every { cryptoManager invokeNoArgs "getRsaEncryptionPublicKey" } returns publicKey
        every { Cipher.getInstance(any()) } returns cipher
        every { cipher.init(Cipher.ENCRYPT_MODE, any<PublicKey>()) } just Runs
        every { cipher.doFinal(any<ByteArray>()) } returns ByteArray(512) { 3 }

        val result = cryptoManager.rsaEncrypt(plaintext)

        assertTrue(result is AppResult.Success)
        assertEquals(512, (result as AppResult.Success).data.size)
    }

    @Test
    fun `rsaDecrypt should decrypt successfully`() = runBlocking {
        val encryptedData = ByteArray(512) { 3 }
        val privateKey = mockk<PrivateKey>()
        val cipher = mockk<Cipher>()

        every { cryptoManager invokeNoArgs "getRsaEncryptionPrivateKey" } returns privateKey
        every { Cipher.getInstance(any()) } returns cipher
        every { cipher.init(Cipher.DECRYPT_MODE, any<PrivateKey>()) } just Runs
        every { cipher.doFinal(any<ByteArray>()) } returns "SecureData".toByteArray()

        val result = cryptoManager.rsaDecrypt(encryptedData)

        assertTrue(result is AppResult.Success)
        assertEquals("SecureData", String((result as AppResult.Success).data))
    }

    @Test
    fun `signData should return a valid signature`() = runBlocking {
        val data = "TestSignature".toByteArray()
        val privateKey = mockk<PrivateKey>()
        val signature = mockk<Signature>()

        every { cryptoManager invokeNoArgs "getRsaSigningPrivateKey" } returns privateKey
        every { Signature.getInstance(any()) } returns signature
        every { signature.initSign(any<PrivateKey>()) } just Runs
        every { signature.update(any<ByteArray>()) } just Runs
        every { signature.sign() } returns ByteArray(256) { 4 }

        val result = cryptoManager.signData(data)

        assertTrue(result is AppResult.Success)
        assertEquals(256, (result as AppResult.Success).data.size)
    }

    @Test
    fun `verifySignature should confirm valid signatures`() = runBlocking {
        val data = "TestSignature".toByteArray()
        val signatureBytes = ByteArray(256) { 4 }
        val publicKey = mockk<PublicKey>()
        val signature = mockk<Signature>()

        every { cryptoManager invokeNoArgs "getRsaSigningPublicKey" } returns publicKey
        every { Signature.getInstance(any()) } returns signature
        every { signature.initVerify(any<PublicKey>()) } just Runs
        every { signature.update(any<ByteArray>()) } just Runs
        every { signature.verify(any<ByteArray>()) } returns true

        val result = cryptoManager.verifySignature(data, signatureBytes)

        assertTrue(result is AppResult.Success)
        assertTrue((result as AppResult.Success).data)
    }

    @Test
    fun `hashWithSalt should generate a valid hash`() = runBlocking {
        val data = "Password123"
        val expectedHash = "pbkdf2_sha256:600000:random_salt:random_hash"

        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), Base64.NO_WRAP) } returns "random_salt" andThen "random_hash"

        val result = cryptoManager.hashWithSalt(data)

        assertTrue(result is AppResult.Success)
        assertEquals(expectedHash, (result as AppResult.Success).data)
    }

    @Test
    fun `verifyHash should confirm a valid password hash`() = runBlocking {
        val data = "Password123"
        val validHash = "pbkdf2_sha256:600000:random_salt:random_hash"

        mockkStatic(Base64::class)
        every { Base64.decode("random_salt", Base64.NO_WRAP) } returns ByteArray(16) { 1 }
        every { Base64.decode("random_hash", Base64.NO_WRAP) } returns ByteArray(32) { 2 }
        every { Base64.encodeToString(any(), Base64.NO_WRAP) } returns "random_hash"

        val result = cryptoManager.verifyHash(data, validHash)

        assertTrue(result is AppResult.Success)
        assertTrue((result as AppResult.Success).data)
    }
}
