package com.estatia.realestate.apps.core.domain.config

interface ISecurityConfig : IConfigLifecycle {
    val isLoggingEnabled: Boolean
    val isTelemetryEnabled: Boolean
    val googleKeyPattern: Regex
    val genericKeyPattern: Regex
    val paymentsKeyPattern: Regex
    val encryptionLocationId: String
    val encryptionKeyRingId: String
    val symmetricKeyId: String
    val asymmetricKeyId: String
    val asymmetricSigningKeyId: String
}
