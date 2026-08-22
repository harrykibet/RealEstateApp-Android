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
              "network": {
                "base_url": "https://api.estatia.com",
                "cdn_endpoints": [
                  {
                    "name": "Primary",
                    "base_url": "https://cdn1.estatia.com"
                  }
                ]
              },
              "security": {
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
                "enable_logging": true
              }
            }
        """.trimIndent()

        val result = parser.parse(json)

        assertEquals("^AIza[0-9A-Za-z_-]{35}$", result.security.keyPatterns.google)
        assertEquals("us-central1", result.security.encryptionKeys.locationId)
        assertEquals(1, result.network.cdnEndpoints.size)
        assertEquals("Primary", result.network.cdnEndpoints[0].name)
        assertEquals("https://api.estatia.com", result.network.baseUrl)
        assertTrue(result.security.enableLogging)
    }

    @Test
    fun `parseNetwork returns populated model`() {
        val json = """
            {
              "base_url": "https://api.estatia.com",
              "cdn_endpoints": []
            }
        """.trimIndent()
        val result = parser.parseNetwork(json)
        assertEquals("https://api.estatia.com", result.baseUrl)
    }

    @Test(expected = Exception::class)
    fun `parse invalid config throws exception`() {
        val json = """{"invalid": "data"}"""
        parser.parse(json)
    }
}
