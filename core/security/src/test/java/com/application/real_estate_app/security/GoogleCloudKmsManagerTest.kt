package com.application.real_estate_app.security

import android.util.Base64
import com.application.real_estate_app.core_common.interfaces.IRemoteConfigManager
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.security.data.sources.remote.GoogleCloudKmsManager
import com.application.real_estate_app.security.utils.exceptions.CryptoOperationException
import com.application.real_estate_app.security.utils.exceptions.GoogleKmsException
import com.google.api.gax.rpc.ApiException
import com.google.cloud.kms.v1.*
import com.google.protobuf.ByteString
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import java.security.GeneralSecurityException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GoogleCloudKmsManagerTest {

    private lateinit var kmsClient: KeyManagementServiceClient
    private lateinit var remoteConfig: IRemoteConfigManager
    private lateinit var logger: LoggerInterface
    private lateinit var googleCloudKmsManager: GoogleCloudKmsManager

    @BeforeAll
    fun setup() {
        kmsClient = mockk()
        remoteConfig = mockk()
        logger = mockk(relaxed = true)

        every { remoteConfig.getSymmetricKeyId() } returns "symmetric-key-id"
        every { remoteConfig.getAsymmetricKeyId() } returns "asymmetric-key-id"
        every { remoteConfig.getAsymmetricSigningKeyId() } returns "signing-key-id"
        every { remoteConfig.getKeyRingLocationId() } returns "global"
        every { remoteConfig.getKeyRingId() } returns "my-key-ring"

        googleCloudKmsManager = GoogleCloudKmsManager(kmsClient, remoteConfig, "test-project", logger)
    }

    @Test
    fun `encryptDataSymmetric should return encrypted data`() = runBlocking {
        val plaintext = "HelloWorld"
        val encryptedBytes = ByteString.copyFromUtf8("EncryptedData")
        val encryptResponse = EncryptResponse.newBuilder().setCiphertext(encryptedBytes).build()

        // Explicitly specify the String overload
        every { kmsClient.encrypt(any<String>(), any()) } returns encryptResponse

        val encryptedResult = googleCloudKmsManager.encryptDataSymmetric(plaintext)

        Assertions.assertEquals(Base64.encodeToString(encryptedBytes.toByteArray(), Base64.NO_WRAP), encryptedResult)
    }


    @Test
    fun `encryptDataSymmetric should throw CryptoOperationException on failure`() = runBlocking {
        val plaintext = "HelloWorld"

        every { kmsClient.encrypt(any<String>(), any()) } throws ApiException(null, null, false)

        val exception = assertThrows(GoogleKmsException::class.java) {
            runBlocking { googleCloudKmsManager.encryptDataSymmetric(plaintext) }
        }
        Assertions.assertTrue(exception.message!!.contains("KMS operation failed"))
    }

    @Test
    fun `decryptDataSymmetric should return decrypted data`() = runBlocking {
        val decryptedBytes = ByteString.copyFromUtf8("DecryptedData")
        val decryptResponse = DecryptResponse.newBuilder().setPlaintext(decryptedBytes).build()

        every { kmsClient.decrypt(any<String>(), any()) } returns decryptResponse

        val encryptedData = Base64.encodeToString("EncryptedData".toByteArray(), Base64.NO_WRAP)
        val decryptedResult = googleCloudKmsManager.decryptDataSymmetric(encryptedData)

        Assertions.assertEquals("DecryptedData", decryptedResult)
    }

    @Test
    fun `decryptDataSymmetric should throw CryptoOperationException on failure`() = runBlocking {
        val encryptedData = Base64.encodeToString("EncryptedData".toByteArray(), Base64.NO_WRAP)

        every { kmsClient.decrypt(any<String>(), any()) } throws GeneralSecurityException("Decryption error")

        val exception = assertThrows(CryptoOperationException::class.java) {
            runBlocking { googleCloudKmsManager.decryptDataSymmetric(encryptedData) }
        }
        Assertions.assertTrue(exception.message!!.contains("Security operation failed"))
    }

    @Test
    fun `listKeys should return a list of key names`() = runBlocking {
        val cryptoKey = CryptoKey.newBuilder().setName("projects/test/locations/global/keyRings/my-key-ring/cryptoKeys/key1").build()
        val mockPagedResponse = mockk<KeyManagementServiceClient.ListCryptoKeysPagedResponse>()

        every { mockPagedResponse.iterateAll() } returns listOf(cryptoKey)
        every { kmsClient.listCryptoKeys(any<String>()) } returns mockPagedResponse

        val keyList = googleCloudKmsManager.listKeys()

        Assertions.assertEquals(1, keyList.size)
        Assertions.assertTrue(keyList.contains("projects/test/locations/global/keyRings/my-key-ring/cryptoKeys/key1"))
    }

    @Test
    fun `listKeys should throw GoogleKmsException when API call fails`() = runBlocking {
        every { kmsClient.listCryptoKeys(any<String>()) } throws ApiException(null, null, false)

        val exception = assertThrows(GoogleKmsException::class.java) {
            runBlocking { googleCloudKmsManager.listKeys() }
        }
        Assertions.assertTrue(exception.message!!.contains("KMS operation failed"))
    }

    @Test
    fun `signData should return signed data`() = runBlocking {
        val data = "TestData"
        val signatureBytes = ByteString.copyFromUtf8("SignatureData")
        val signResponse = AsymmetricSignResponse.newBuilder().setSignature(signatureBytes).build()

        every { kmsClient.asymmetricSign(any<String>(), any()) } returns signResponse

        val signature = googleCloudKmsManager.signData(data)

        Assertions.assertEquals(Base64.encodeToString(signatureBytes.toByteArray(), Base64.NO_WRAP), signature)
    }

    @Test
    fun `verifySignature should return true for valid signature`() = runBlocking {
        val data = "TestData"
        val signature = Base64.encodeToString("ValidSignature".toByteArray(), Base64.NO_WRAP)

        val mockPublicKey = mockk<java.security.PublicKey>()
        every { mockPublicKey.encoded } returns byteArrayOf(1, 2, 3, 4)

        mockkStatic(java.security.Signature::class)
        val mockSignature = mockk<java.security.Signature>()
        every { java.security.Signature.getInstance(any()) } returns mockSignature
        every { mockSignature.initVerify(mockPublicKey) } just Runs
        every { mockSignature.update(any<ByteArray>()) } just Runs
        every { mockSignature.verify(any()) } returns true

        val isValid = googleCloudKmsManager.verifySignature(data, signature)

        Assertions.assertTrue(isValid)
    }
}
