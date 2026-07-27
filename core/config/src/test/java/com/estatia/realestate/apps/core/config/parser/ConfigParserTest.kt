package com.estatia.realestate.apps.core.config.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigParserTest {

    private val parser = ConfigParser()

    @Test
    fun `parse valid config returns populated model`() {
        val json = """
            {
              "key_patterns": {
                "google": "^AIza[0-9A-Za-z_-]{35}$",
                "generic": "^[A-Za-z0-9]{32}$",
                "payments": "^[0-9A-Za-z]{40}$"
              },
              "encryption_keys": {
                "location_id": "us-central1",
                "key_ring_id": "main-ring",
                "symmetric_key_id": "aes-key",
                "asymmetric_key_id": "rsa-key",
                "asymmetric_signing_key_id": "sign-key"
              },
              "cdn_endpoints": [
                {
                  "name": "Primary",
                  "base_url": "https://cdn1.estatia.com"
                }
              ],
              "base_config": {
                "base_url": "https://api.estatia.com",
                "enable_logging": true
              }
            }
        """.trimIndent()

        val result = parser.parse(json)

        assertEquals("^AIza[0-9A-Za-z_-]{35}$", result.keyPatterns.google)
        assertEquals("us-central1", result.encryptionKeys.locationId)
        assertEquals(1, result.cdnEndpoints.size)
        assertEquals("Primary", result.cdnEndpoints[0].name)
        assertEquals("https://api.estatia.com", result.baseConfig.baseUrl)
        assertTrue(result.baseConfig.enableLogging)
    }

    @Test(expected = Exception::class)
    fun `parse invalid config throws exception`() {
        val json = """{"invalid": "data"}"""
        parser.parse(json)
    }
}
