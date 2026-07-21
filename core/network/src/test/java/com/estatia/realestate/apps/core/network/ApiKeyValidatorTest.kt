package com.estatia.realestate.apps.core.network

import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.config.repository.ConfigRepository
import com.estatia.realestate.apps.core.network.interfaces.IApiKeyValidator
import com.estatia.realestate.apps.core.network.utils.ApiKeyValidator
import com.estatia.realestate.apps.core.network.utils.ServiceNames
import com.estatia.realestate.apps.core.common.exceptions.SecurityException.InvalidApiKey as InvalidApiKeyException
import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiKeyValidatorTest {

    private lateinit var logger: ILogger
    private lateinit var config: ConfigRepository
    private lateinit var apiKeyValidator: IApiKeyValidator

    @BeforeAll
    fun setup() {
        logger = mockk(relaxed = true)  // Mock logger, allow relaxed mode to avoid unnecessary stubs
        config = mockk()

        every { config.googleKeyPattern } returns Regex("^AIza[0-9A-Za-z_-]{35}$")
        every { config.genericKeyPattern } returns Regex("^[A-Za-z0-9]{32}$")
        every { config.paymentsKeyPattern } returns Regex("^[0-9A-Za-z]{40}$")

        apiKeyValidator = ApiKeyValidator(logger, config)
    }

    @Test
    fun `validate should throw exception when API key is empty`() {
        val exception = assertThrows(InvalidApiKeyException::class.java) {
            apiKeyValidator.validate("", null)
        }
        Assertions.assertEquals("API key cannot be empty", exception.message)
    }

    @Test
    fun `validate should throw exception for invalid Google API key`() {
        val invalidKey = "INVALID_KEY"

        val exception = assertThrows(InvalidApiKeyException::class.java) {
            apiKeyValidator.validate(invalidKey, ServiceNames.MAPS)
        }

        Assertions.assertTrue(exception.message!!.contains("Invalid API key format for MAPS"))
        verify { logger.e(message = match { it.contains("Invalid API key format for MAPS") }) }
    }

    @Test
    fun `validate should pass for a valid Google API key`() {
        val validGoogleKey = "AIzaSyD12345678901234567890123456789ABC"

        apiKeyValidator.validate(validGoogleKey, ServiceNames.PLACES)

        verify { logger.d(message = match { it.contains("Validated API key for service") }) }
    }

    @Test
    fun `validate should throw exception for invalid generic API key`() {
        val invalidGenericKey = "SHORTKEY"

        val exception = assertThrows(InvalidApiKeyException::class.java) {
            apiKeyValidator.validate(invalidGenericKey, null)
        }

        Assertions.assertTrue(exception.message!!.contains("Invalid generic API key format"))
        verify { logger.e(message = match { it.contains("Invalid generic API key format") }) }
    }

    @Test
    fun `validate should pass for a valid generic API key`() {
        val validGenericKey = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6"

        apiKeyValidator.validate(validGenericKey, null)

        verify { logger.d(message = match { it.contains("Validated API key for service") }) }
    }

    @Test
    fun `sanitizeForLogging should redact short keys`() {
        val shortKey = "12345"
        val sanitized = apiKeyValidator.sanitizeForLogging(shortKey)

        Assertions.assertEquals("[REDACTED]", sanitized)
    }

    @Test
    fun `sanitizeForLogging should keep first and last 4 characters`() {
        val key = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6"
        val sanitized = apiKeyValidator.sanitizeForLogging(key)

        Assertions.assertEquals("A1B2...O5P6", sanitized)
    }
}
