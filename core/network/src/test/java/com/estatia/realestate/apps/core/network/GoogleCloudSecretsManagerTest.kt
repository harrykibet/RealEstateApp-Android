package com.estatia.realestate.apps.core.network

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.interfaces.IGoogleCloudSecretsManager
import com.estatia.realestate.apps.core.network.utils.SensitiveString
import com.estatia.realestate.apps.core.network.sources.GoogleCloudSecretsManager
import com.estatia.realestate.apps.core.network.exceptions.SecretsManagerException
import com.estatia.realestate.apps.core.model.utils.SemanticVersion
import com.estatia.realestate.apps.core.model.security.CacheKey
import com.estatia.realestate.apps.core.model.security.SecretId
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.google.cloud.secretmanager.v1.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.concurrent.CompletableFuture

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GoogleCloudSecretsManagerTest {

    private lateinit var secretManagerClient: SecretManagerServiceClient
    private lateinit var logger: LoggerInterface
    private lateinit var cache: AsyncLoadingCache<CacheKey, SensitiveString>
    private lateinit var googleCloudSecretsManager: IGoogleCloudSecretsManager

    @BeforeAll
    fun setup() {
        secretManagerClient = mockk()
        logger = mockk(relaxed = true)
        cache = mockk()

        googleCloudSecretsManager = GoogleCloudSecretsManager(secretManagerClient, "test-project", logger, cache)
    }

    @Test
    fun `getSecret should return a secret successfully`() = runBlocking {
        val secretId = "test-secret"
        val version = "1.0.0"
        val context = mapOf("environment" to "staging")
        val mockKey = CacheKey(SecretId(secretId), SemanticVersion.parse(version), "staging")
        val secretValue = SensitiveString.fromSecureString("mock-secret-value")

        every { cache.get(mockKey) } returns CompletableFuture.completedFuture(secretValue)

        val result = googleCloudSecretsManager.getSecret(secretId, version, context)

        Assertions.assertTrue(result.isSuccess)
        Assertions.assertEquals(secretValue, result.getOrThrow())
    }

    @Test
    fun `getSecret should throw SecretsManagerException when retrieval fails`() {
        runBlocking {
            val secretId = "test-secret"
            val version = "1.0.0"
            val context = emptyMap<String, String>()
            val mockKey = CacheKey(SecretId(secretId), SemanticVersion.parse(version), "production")

            every { cache.get(mockKey) } throws RuntimeException("Secret retrieval failed")

            val result = googleCloudSecretsManager.getSecret(secretId, version, context)

            Assertions.assertTrue(result.isFailure)
            assertThrows(SecretsManagerException::class.java) {
                result.getOrThrow()
            }
        }
    }

    @Test
    fun `preloadSecrets should return successfully cached secrets`() = runBlocking {
        val keys = setOf(CacheKey(SecretId("test-secret"), SemanticVersion.parse("1.0.0"), "production"))
        val mockSecret = SensitiveString.fromSecureString("cached-secret")

        every { cache.getAll(keys) } returns CompletableFuture.completedFuture(
            mapOf(keys.first() to mockSecret)
        )

        val result = googleCloudSecretsManager.preloadSecrets(keys)

        Assertions.assertEquals(1, result.size)
        Assertions.assertTrue(result[keys.first()]!!.isSuccess)
        Assertions.assertEquals(mockSecret, result[keys.first()]!!.getOrThrow())
    }

    @Test
    fun `evictSecretFromCache should remove the secret from cache`() = runBlocking {
        val secretId = SecretId("test-secret")
        val mockKeys = setOf(
            CacheKey(secretId, SemanticVersion.parse("1.0.0"), "production"),
            CacheKey(secretId, SemanticVersion.parse("2.0.0"), "production")
        ).toMutableSet() // ✅ Convert to MutableSet

        every { cache.synchronous().asMap().keys } returns mockKeys
        every { cache.synchronous().invalidate(any()) } just Runs

        googleCloudSecretsManager.evictSecretFromCache(secretId)

        verify(exactly = 2) { cache.synchronous().invalidate(any()) }
    }

    @Test
    fun `getLatestStableVersion should return latest stable version`() = runBlocking {
        val secretId = SecretId("test-secret")
        val secretName = SecretName.of("test-project", secretId.value)
        val secretVersion = SecretVersion.newBuilder().setName("projects/test/secretVersions/5")
            .setState(SecretVersion.State.ENABLED).build()

        val mockPagedResponse = mockk<SecretManagerServiceClient.ListSecretVersionsPagedResponse>()
        every { mockPagedResponse.iterateAll() } returns listOf(secretVersion)
        every { secretManagerClient.listSecretVersions(secretName) } returns mockPagedResponse

        val result = googleCloudSecretsManager.getLatestStableVersion(secretId)

        Assertions.assertEquals("5", result)
    }

    @Test
    fun `getLatestStableVersion should throw exception if no enabled versions exist`() = runBlocking {
        val secretId = SecretId("test-secret")
        val secretName = SecretName.of("test-project", secretId.value)

        val mockPagedResponse = mockk<SecretManagerServiceClient.ListSecretVersionsPagedResponse>()
        every { mockPagedResponse.iterateAll() } returns emptyList()
        every { secretManagerClient.listSecretVersions(secretName) } returns mockPagedResponse

        val exception = assertThrows(SecretsManagerException::class.java) {
            runBlocking { googleCloudSecretsManager.getLatestStableVersion(secretId) }
        }
        Assertions.assertTrue(exception.message!!.contains("No stable version"))
    }
}
