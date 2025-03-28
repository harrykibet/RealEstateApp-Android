package com.application.real_estate_app.security.data.sources.remote

import android.os.Build
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.security.domain.interfaces.IGoogleCloudSecretsManager
import com.application.real_estate_app.security.domain.models.CacheKey
import com.application.real_estate_app.security.domain.models.SecretId
import com.application.real_estate_app.security.utils.exceptions.SecretsManagerException
import com.application.real_estate_app.security.utils.extensions.SemanticVersion
import com.application.real_estate_app.security.utils.extensions.SensitiveString
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretName
import com.google.cloud.secretmanager.v1.SecretVersion
import com.google.cloud.secretmanager.v1.SecretVersionName
import io.micrometer.core.instrument.Metrics
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.Tracer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCloudSecretsManager @Inject constructor(
    private val secretManagerClient: SecretManagerServiceClient,
    private val projectId: String,
    private val logger: LoggerInterface,
    private val cache: AsyncLoadingCache<CacheKey, SensitiveString>
) : IGoogleCloudSecretsManager {

    private val tracer: Tracer = GlobalOpenTelemetry.getTracer("google-secrets-manager")

    init {
        Metrics.gauge("cache.size", cache) { it.synchronous().estimatedSize().toDouble() }
        logger.i("SecretsManager initialized for project: $projectId")
    }

    override suspend fun getSecret(
        secretId: String,
        version: String,
        context: Map<String, String>
    ): Result<SensitiveString> = withContext(Dispatchers.IO) {
        val span = buildSpan("getSecret", secretId, version)
        return@withContext try {
            val key = createCacheKey(secretId, version, context)
            val secret = executeWithRetries { cache.get(key).await().also { validateSecret(it) } }
            logSuccess(secretId)
            Result.success(secret)
        } catch (e: Exception) {
            handleError(span, e, secretId)
            Result.failure(SecretsManagerException(e.message ?: "Unknown error"))
        }
    }

    override suspend fun preloadSecrets(keys: Set<CacheKey>): Map<CacheKey, Result<SensitiveString>> {
        return cache.getAll(keys).await().mapValues { entry ->
            entry.value?.let { Result.success(it) } ?: Result.failure(
                SecretsManagerException("Preload failed for ${entry.key}")
            )
        }
    }

    override suspend fun evictSecretFromCache(secretId: SecretId) {
        cache.synchronous().asMap().keys.filter { it.secretId == secretId }.forEach { cache.synchronous().invalidate(it) }
    }

    override suspend fun getLatestStableVersion(secretId: SecretId): String {
        val secretName = SecretName.of(projectId, secretId.value)
        return secretManagerClient.listSecretVersions(secretName)
            .iterateAll()
            .filter { it.state == SecretVersion.State.ENABLED }
            .maxByOrNull { it.createTime.seconds * 1000 + it.createTime.nanos / 1_000_000 }
            ?.name?.split("/")?.lastOrNull()
            ?: throw SecretsManagerException("No stable version for $secretId")
    }

    private fun buildSpan(operation: String, secretId: String, version: String): Span {
        return tracer.spanBuilder(operation)
            .setAttribute("secret.id", secretId)
            .setAttribute("secret.version", version)
            .setAttribute("project.id", projectId)
            .startSpan()
    }

    private fun createCacheKey(secretId: String, version: String, context: Map<String, String>) = CacheKey(
        secretId = SecretId(secretId),
        version = SemanticVersion.parse(version),
        environment = context["environment"] ?: "production"
    )

    private suspend fun executeWithRetries(block: suspend () -> SensitiveString): SensitiveString {
        var attempts = 0
        while (true) {
            try {
                return block()
            } catch (e: SecretsManagerException.Retryable) {
                if (++attempts >= 3) throw e
            }
        }
    }

    private fun validateSecret(secret: SensitiveString) {
        when {
            secret.isEmpty() -> throw SecretsManagerException("Empty secret value")
            secret.isBlacklisted() -> throw SecretsManagerException("Blacklisted pattern detected")
        }
    }

    private fun logSuccess(secretId: String) {
        Metrics.counter("secret.access", "id", secretId).increment()
        logger.d("Successfully retrieved secret: $secretId")
    }

    private fun handleError(span: Span, e: Exception, secretId: String) {
        span.recordException(e)
        Metrics.counter("secret.errors", "id", secretId, "type", e.javaClass.simpleName).increment()
        logger.e("Failed to retrieve secret: $secretId", e)
    }

    companion object {
        fun create(
            secretManagerClient: SecretManagerServiceClient,
            projectId: String,
            logger: LoggerInterface
        ): GoogleCloudSecretsManager {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                GoogleCloudSecretsManager(
                    secretManagerClient = secretManagerClient,
                    projectId = projectId,
                    logger = logger,
                    cache = Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .evictionListener { _: CacheKey?, value: SensitiveString?, _: RemovalCause -> value?.clear() }
                        .buildAsync { key ->
                            secretManagerClient.accessSecretVersion(
                                SecretVersionName.of(projectId, key.secretId.value, key.version.toString())
                            ).payload.data.toStringUtf8().let { SensitiveString.fromSecureString(it) }
                        }
                )
            } else {
                throw UnsupportedOperationException("GoogleCloudSecretsManager requires Android O (API 26) or later.")
            }
        }
    }
}
