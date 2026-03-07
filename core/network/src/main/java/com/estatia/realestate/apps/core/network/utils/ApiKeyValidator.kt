package com.estatia.realestate.apps.core.network.utils

import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.config.repository.ConfigRepository
import com.estatia.realestate.apps.core.network.exceptions.InvalidApiKeyException
import com.estatia.realestate.apps.core.network.interfaces.IApiKeyValidator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ApiKeyValidator] is responsible for validating API keys against predefined patterns
 * retrieved from a remote configuration. It supports validation for specific services
 * as well as a generic validation for non-service-specific keys.
 *
 * This class utilizes regular expressions to match the expected format of API keys
 * and provides sanitization for logging purposes to prevent exposing sensitive information.
 *
 * @property logger An instance of [LoggerInterface] for logging validation results and errors.
 * @property config An instance of [ConfigRepository] for retrieving API key patterns from remote config.
 */
@Singleton
class ApiKeyValidator @Inject constructor(
    private val logger: LoggerInterface,
    private val config: ConfigRepository
) : IApiKeyValidator {

    private val googleKeyPattern: Regex
        get() = config.googleKeyPattern

    private val genericKeyPattern: Regex
        get() = config.genericKeyPattern

    private val paymentsKeyPattern: Regex
        get() = config.paymentsKeyPattern

    private val servicePatterns: Map<ServiceNames, Regex>
        get() = mapOf(
            ServiceNames.MAPS to googleKeyPattern,
            ServiceNames.PLACES to googleKeyPattern,
            ServiceNames.PAYMENTS to paymentsKeyPattern,
            ServiceNames.AUTH to googleKeyPattern
        )

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
        val pattern = servicePatterns[service]
            ?: throw IllegalArgumentException("No validation pattern defined for ${service.name}")

        if (!pattern.matches(apiKey)) {
            val error = "Invalid API key format for ${service.name}. Sanitized key: $sanitizedKey"
            logger.e(error)
            throw InvalidApiKeyException(error, service)
        }
    }

    private fun performGenericValidation(apiKey: String, sanitizedKey: String) {
        if (!genericKeyPattern.matches(apiKey)) {
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
