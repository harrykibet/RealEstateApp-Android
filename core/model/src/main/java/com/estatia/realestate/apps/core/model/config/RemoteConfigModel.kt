package com.estatia.realestate.apps.core.model.config

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint

data class RemoteConfigModel(
    val keyPatterns: KeyPatterns,
    val encryptionKeys: EncryptionKeys,
    val cdnEndpoints: List<CdnEndpoint>,
    val baseConfig: BaseConfig,
    val chaosConfig: ChaosConfig = ChaosConfig()
)

data class KeyPatterns(
    val google: String,
    val generic: String,
    val payments: String
)

data class EncryptionKeys(
    val locationId: String,
    val keyRingId: String,
    val symmetricKeyId: String,
    val asymmetricKeyId: String,
    val asymmetricSigningKeyId: String
)

data class BaseConfig(
    val baseUrl: String,
    val enableLogging: Boolean
)

data class ChaosConfig(
    val simulateStall: Boolean = false,
    val stallDurationMs: Long = 0,
    val failureRate: Float = 0f,
    val throttleBps: Long = 0
)
