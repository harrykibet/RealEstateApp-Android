package com.estatia.realestate.apps.core.network.utils

import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.network.interfaces.IApiKeyValidator
import com.estatia.realestate.apps.core.network.BuildConfig
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
 * @property logger An instance of [ILogger] for logging validation results and errors.
 * @property config An instance of [IConfigProvider] for retrieving API key patterns from remote config.
 */
@Singleton
internal class ApiKeyValidator @Inject constructor(
    private val logger: ILogger,
    private val config: IConfigProvider,
) : IApiKeyValidator {

    private val googleKeyPattern: Regex
        get() = config.googleKeyPattern

    private val genericKeyPattern: Regex
        get() = config.genericKeyPattern

    private val paymentsKeyPattern: Regex
        get() = config.paymentsKeyPattern

    private val servicePatterns: Map<ServiceNames, Regex>
        get() = mapOf(
            ServiceNames.PAYMENTS to paymentsKeyPattern,
            ServiceNames.AUTH to googleKeyPattern,
        )

    override fun validate(apiKey: String, service: ServiceNames?) {
        val sanitizedKey = sanitizeForLogging(apiKey)

        when {
            apiKey.isEmpty() -> throw SecurityException.InvalidApiKey("API key cannot be empty + $service")
            service != null -> validateForService(apiKey, service, sanitizedKey)
            else -> performGenericValidation(apiKey, sanitizedKey)
        }

        if (BuildConfig.DEBUG) {
            logger.d(message = "Validated API key for service ${service?.name ?: "Generic"}: $sanitizedKey")
        }
    }

    private fun validateForService(apiKey: String, service: ServiceNames, sanitizedKey: String) {
        val pattern = servicePatterns[service]
            ?: throw SecurityException.InvalidApiKey("No matching api key pattern for: ${service.name}")

        if (!pattern.matches(apiKey)) {
            val error = "Invalid API key format for ${service.name}. Sanitized key: $sanitizedKey"
            logger.e(message = error)
            throw SecurityException.InvalidApiKey(error)
        }
    }

    private fun performGenericValidation(apiKey: String, sanitizedKey: String) {
        if (!genericKeyPattern.matches(apiKey)) {
            val error = "Invalid generic API key format. Sanitized key: $sanitizedKey"
            logger.e(message = error)
            throw SecurityException.InvalidApiKey(error)
        }
    }

    override fun sanitizeForLogging(apiKey: String): String {
        return when {
            apiKey.length <= 8 -> "[REDACTED]"
            else -> "${apiKey.take(4)}...${apiKey.takeLast(4)}"
        }
    }
}
