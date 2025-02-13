package com.application.real_estate_app.security.utils.extensions

import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.security.domain.interfaces.IApiKeyValidator
import com.application.real_estate_app.security.utils.exceptions.InvalidApiKeyException
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class ApiKeyValidator @Inject constructor(
    private val logger: LoggerInterface
) : IApiKeyValidator {

    companion object {
        // Google API key pattern
        private val GOOGLE_KEY_PATTERN = Regex("^AIza[0-9A-Za-z-_]{35}\$")

        // Standard API key pattern (most services)
        private val GENERIC_KEY_PATTERN = Regex("^[A-Za-z0-9-_]{32,64}\$")

        // Service-specific patterns
        private val SERVICE_PATTERNS = mapOf(
            ServiceNames.MAPS to GOOGLE_KEY_PATTERN,
            ServiceNames.PLACES to GOOGLE_KEY_PATTERN,
            ServiceNames.PAYMENTS to Regex("^(pk|sk)_(test|live)_[0-9a-zA-Z]{24}\$"),
            ServiceNames.AUTH to GOOGLE_KEY_PATTERN
        )
    }

    override fun validate(apiKey: String, service: ServiceNames?) {
        val sanitizedKey = sanitizeForLogging(apiKey)

        when {
            apiKey.isEmpty() -> throw InvalidApiKeyException("API key cannot be empty", service)
            service != null -> validateForService(apiKey, service, sanitizedKey)
            else -> performGenericValidation(apiKey, sanitizedKey)
        }

        logger.d("Validated API key for service {}: {}, ${service?.name}, $sanitizedKey")
    }

    private fun validateForService(apiKey: String, service: ServiceNames, sanitizedKey: String) {
        val pattern = SERVICE_PATTERNS[service]
            ?: throw IllegalArgumentException("No validation pattern defined for ${service.name}")

        if (!pattern.matches(apiKey)) {
            val error = "Invalid API key format for ${service.name}. Sanitized key: $sanitizedKey"
            logger.e(error)
            throw InvalidApiKeyException(error, service)
        }
    }

    private fun performGenericValidation(apiKey: String, sanitizedKey: String) {
        if (!GENERIC_KEY_PATTERN.matches(apiKey)) {
            val error = "Invalid generic API key format. Sanitized key: $sanitizedKey"
            logger.e(error)
            throw InvalidApiKeyException(error)
        }
    }

    override fun sanitizeForLogging(apiKey: String): String {
        return when {
            apiKey.length <= 8 -> "[REDACTED]"
            else -> "${apiKey.take(4)}...${apiKey.takeLast(4)}"
        }
    }
}