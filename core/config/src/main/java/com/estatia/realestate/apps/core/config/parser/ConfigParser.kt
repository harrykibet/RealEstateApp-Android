package com.estatia.realestate.apps.core.config.parser

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.model.config.*
import kotlinx.serialization.json.*

// ConfigParser.kt
class ConfigParser {

    fun parse(json: String): RemoteConfigModel {
        val root = Json.parseToJsonElement(json).jsonObject

        val keyPatterns = root["key_patterns"]!!.jsonObject
        val encryptionKeys = root["encryption_keys"]!!.jsonObject
        val baseConfig = root["base_config"]!!.jsonObject

        val cdnEndpoints = root["cdn_endpoints"]!!
            .jsonArray
            .map { element ->
                val obj = element.jsonObject
                CdnEndpoint(
                    name = obj["name"]!!.jsonPrimitive.content,
                    baseUrl = obj["base_url"]!!.jsonPrimitive.content
                )
            }

        return RemoteConfigModel(
            keyPatterns = KeyPatterns(
                google = keyPatterns["google"]!!.jsonPrimitive.content,
                generic = keyPatterns["generic"]!!.jsonPrimitive.content,
                payments = keyPatterns["payments"]!!.jsonPrimitive.content
            ),
            encryptionKeys = EncryptionKeys(
                locationId = encryptionKeys["location_id"]!!.jsonPrimitive.content,
                keyRingId = encryptionKeys["key_ring_id"]!!.jsonPrimitive.content,
                symmetricKeyId = encryptionKeys["symmetric_key_id"]!!.jsonPrimitive.content,
                asymmetricKeyId = encryptionKeys["asymmetric_key_id"]!!.jsonPrimitive.content,
                asymmetricSigningKeyId = encryptionKeys["asymmetric_signing_key_id"]!!.jsonPrimitive.content
            ),
            cdnEndpoints = cdnEndpoints,
            baseConfig = BaseConfig(
                baseUrl = baseConfig["base_url"]!!.jsonPrimitive.content,
                enableLogging = baseConfig["enable_logging"]!!.jsonPrimitive.boolean
            )
        )
    }
}
