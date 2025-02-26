package com.application.real_estate_app.security.data.sources.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
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
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.micrometer.core.instrument.Metrics
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.Tracer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


/**
 * [GoogleCloudSecretsManager] is a class responsible for retrieving secrets from Google Cloud Secret Manager.
 *
 * It leverages caching, circuit breaking, and retry mechanisms to provide a resilient and efficient way to access secrets.
 * It also integrates with OpenTelemetry for tracing and metrics.
 *
 * @property secretManagerClient The Google Cloud Secret Manager client.
 * @property projectId The Google Cloud project ID.
 * @property logger The logger instance for logging messages.
 * @property circuitBreaker The circuit breaker instance for handling service failures.
 * @property retry The retry instance for handling transient errors.
 * @property cache The cache */
@Singleton
class GoogleCloudSecretsManager @Inject constructor(
    private val secretManagerClient: SecretManagerServiceClient,
    private val projectId: String,
    private val logger: LoggerInterface,
    private val circuitBreaker: CircuitBreaker,
    private val retry: Retry,
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
        try {
            span.let {
                val key = createCacheKey(secretId, version, context)
                val secret = executeWithResilience(key)
                logSuccess(secretId)
                Result.success(secret)
            }
        } catch (e: Exception) {
            handleError(span, e, secretId)
            Result.failure(SecretsManagerException(e.message!!))
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
        val keysToRemove = cache.synchronous().asMap().keys
            .filter { it.secretId == secretId }

        keysToRemove.forEach { key ->
            cache.synchronous().invalidate(key)
        }
    }

    override suspend fun getLatestStableVersion(secretId: SecretId): String {
        val secretName = SecretName.of(projectId, secretId.value)

        return secretManagerClient.listSecretVersions(secretName)
            .iterateAll()
            .filter { it.state == SecretVersion.State.ENABLED }
            .maxByOrNull {
                it.createTime.seconds * 1000 + it.createTime.nanos / 1_000_000
            }
            ?.let { version ->
                version.name.split("/").lastOrNull() ?: throw SecretsManagerException("Invalid version format")
            } ?: throw SecretsManagerException("No stable version for $secretId")
    }

    private fun buildSpan(operation: String, secretId: String, version: String): Span {
        return tracer.spanBuilder(operation)
            .setAttribute("secret.id", secretId)
            .setAttribute("secret.version", version)
            .setAttribute("project.id", projectId)
            .startSpan()
    }

    private fun createCacheKey(
        secretId: String,
        version: String,
        context: Map<String, String>
    ) = CacheKey(
        secretId = SecretId(secretId),
        version = SemanticVersion.parse(version),
        environment = context["environment"] ?: "production"
    )

    private suspend fun executeWithResilience(key: CacheKey): SensitiveString {
        return circuitBreaker.executeSuspendFunction {
            retry.executeSuspendFunction {
                cache.get(key).await().also { validateSecret(it) }
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
                    circuitBreaker = CircuitBreaker.of(
                        "secrets-cb",
                        CircuitBreakerConfig.custom()
                            .failureRateThreshold(40.0F)
                            .waitDurationInOpenState(Duration.ofSeconds(45))
                            .slidingWindow(10, 10,
                                CircuitBreakerConfig.SlidingWindowType.COUNT_BASED,
                                CircuitBreakerConfig.SlidingWindowSynchronizationStrategy.SYNCHRONIZED)
                            .build()
                    ),
                    retry = Retry.of("secrets-retry", RetryConfig.custom<Any>()
                        .maxAttempts(3)
                        .waitDuration(Duration.ofMillis(200))
                        .retryOnException { e -> e is SecretsManagerException.Retryable }
                        .build()),
                    cache = Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .evictionListener { _: CacheKey?, value: SensitiveString?, _: RemovalCause ->
                            value?.clear()
                        }
                        .buildAsync { key ->
                            secretManagerClient.accessSecretVersion(
                                SecretVersionName.of(
                                    projectId,
                                    key.secretId.value,
                                    key.version.toString()
                                )
                            ).payload.data.toStringUtf8()
                                .let { SensitiveString.fromSecureString(it) }
                        }
                )
            } else {
                throw UnsupportedOperationException("GoogleCloudSecretsManager requires Android O (API 26) or later.")
            }
        }
    }
}

