package com.estatia.realestate.apps.core.config.model

data class RemoteConfigModel(
    val keyPatterns: KeyPatterns,
    val encryptionKeys: EncryptionKeys,
    val cdnEndpoints: List<String>,
    val baseConfig: BaseConfig
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